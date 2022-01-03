package com.lablizards.restahead.generation;

import com.lablizards.restahead.conversion.GenericReference;
import com.lablizards.restahead.exceptions.RequestFailedException;
import com.lablizards.restahead.exceptions.RestException;
import com.lablizards.restahead.modeling.declaration.ReturnAdapterCall;
import com.lablizards.restahead.modeling.declaration.ReturnDeclaration;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Predicate;

/**
 * Create a response for the specified type.
 */
public class ResponseGenerator {
    private static final String CONVERTED_NAME = "convertedResponse";
    private static final String RESPONSE_NAME = "response";

    /**
     * Generate an appropriate return statement
     *
     * @param returnDeclaration  the declaration based on which the statement should be generated
     * @param declaredExceptions exceptions declared by the service that don't need to be caught
     * @param builder            the builder to which the return statement should be added
     */
    public void generateReturnStatement(
        ReturnDeclaration returnDeclaration,
        List<TypeMirror> declaredExceptions,
        MethodSpec.Builder builder
    ) {
        if (returnDeclaration.targetConversion().isEmpty() && returnDeclaration.adapterCall().isEmpty()) {
            builder.addStatement("return $L", RESPONSE_NAME);
            return;
        }
        returnDeclaration.targetConversion()
            .ifPresent(returnType -> generateMappingSource(builder, returnType));

        var returnedName = getReturnedVariableName(returnDeclaration.targetConversion().isPresent());
        returnDeclaration.adapterCall().ifPresentOrElse(
            call -> createAdapterCode(call, declaredExceptions, builder, returnedName),
            () -> builder.addStatement("return $L", returnedName)
        );
    }

    /**
     * Create the code for calling the adapter before returning.
     *
     * @param call               the call to adapter
     * @param declaredExceptions the exceptions present on function declaration
     * @param builder            the method builder
     * @param responseName       the name of response variable
     */
    private void createAdapterCode(
        ReturnAdapterCall call,
        List<TypeMirror> declaredExceptions,
        MethodSpec.Builder builder,
        String responseName
    ) {
        var expectedExceptions = call.adapterMethod()
            .exceptions();
        var missingExceptions = expectedExceptions.stream()
            .filter(Predicate.not(declaredExceptions::contains))
            .toList();
        if (!missingExceptions.isEmpty()) {
            builder.beginControlFlow("try");
        }
        builder.addStatement("return $L.$L($L)", call.adapterClass().variableName(), call.adapterMethod().name(), responseName);
        if (!missingExceptions.isEmpty()) {
            var exceptionList = String.join(" | ", missingExceptions.stream().map(e -> "$T").toList());
            builder.nextControlFlow("catch (" + exceptionList + " exception)", missingExceptions.toArray(Object[]::new))
                .addStatement("throw $T.getAppropriateException(exception)", RestException.class)
                .endControlFlow();
        }
    }

    /**
     * Generates the code used to map to another source
     *
     * @param builder    the builder to which the code should be added
     * @param returnType the target mapped return type
     */
    private void generateMappingSource(MethodSpec.Builder builder, TypeMirror returnType) {
        var conversionLambdaBuilder = CodeBlock.builder()
            .beginControlFlow("if (r.status() < 200 || r.status() >= 300)")
            .addStatement("throw new $T(r.status(), r.body())", RequestFailedException.class)
            .endControlFlow()
            .beginControlFlow("try");

        if (isSimpleType(returnType)) {
            conversionLambdaBuilder.addStatement("return converter.deserialize(r, $T.class)", returnType);
        } else {
            conversionLambdaBuilder.addStatement("var conversionTypeHolder = new $T<$T>(){}", GenericReference.class, returnType)
                .addStatement("return converter.deserialize(r, conversionTypeHolder.getType())");
        }

        var conversionLambda = conversionLambdaBuilder.nextControlFlow("catch ($T exception)", IOException.class)
            .addStatement("throw new $T(exception)", CompletionException.class)
            .endControlFlow()
            .build();
        var conversionCaller = CodeBlock.builder()
            .beginControlFlow("$T<$T> $L = response.thenApply(r ->", CompletableFuture.class, returnType, CONVERTED_NAME)
            .add(conversionLambda)
            .endControlFlow(")");
        builder.addCode(conversionCaller.build());
    }

    /**
     * Whether the type is absent from generic parameters
     *
     * @param returnType the type to consider
     * @return false if type has any generic arguments, true otherwise
     */
    private boolean isSimpleType(TypeMirror returnType) {
        if (!(returnType instanceof DeclaredType declaredType)) {
            return true;
        }
        return declaredType.getTypeArguments().isEmpty();
    }

    /**
     * Select the name of the variable to return, if value was not converted, no new variable has been created.
     *
     * @param isConverted if the response has been converted
     * @return the name of the variable
     */
    private String getReturnedVariableName(boolean isConverted) {
        return isConverted ? CONVERTED_NAME : RESPONSE_NAME;
    }
}
