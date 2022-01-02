package com.lablizards.restahead.generation;

import com.lablizards.restahead.conversion.GenericReference;
import com.lablizards.restahead.exceptions.RequestFailedException;
import com.lablizards.restahead.modeling.declaration.ReturnDeclaration;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Create a response for the specified type.
 */
public class ResponseGenerator {
    private static final String CONVERTED_NAME = "convertedResponse";

    /**
     * Generate an appropriate return statement
     *
     * @param returnDeclaration the declaration based on which the statement should be generated
     * @param builder           the builder to which the return statement should be added
     */
    public void generateReturnStatement(
        ReturnDeclaration returnDeclaration,
        MethodSpec.Builder builder
    ) {
        if (returnDeclaration.targetConversion().isEmpty()) {
            builder.addStatement("return response");
            return;
        }
        returnDeclaration.targetConversion()
            .ifPresent(returnType -> generateMappingSource(builder, returnType));

        builder.addStatement("return $L", CONVERTED_NAME);
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
}
