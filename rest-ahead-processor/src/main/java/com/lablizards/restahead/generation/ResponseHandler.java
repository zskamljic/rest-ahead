package com.lablizards.restahead.generation;

import com.lablizards.restahead.client.Response;
import com.lablizards.restahead.conversion.GenericReference;
import com.lablizards.restahead.exceptions.RequestFailedException;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;

/**
 * Create a response for the specified type.
 */
public class ResponseHandler {
    private final Messager messager;
    private final Types types;
    private final TypeMirror futureType;
    private final DeclaredType defaultResponseType;

    /**
     * Creates a new instance.
     *
     * @param elementUtils the Elements to use for class lookup
     */
    public ResponseHandler(Messager messager, Elements elementUtils, Types types) {
        this.messager = messager;
        this.types = types;
        var futureElement = elementUtils.getTypeElement(Future.class.getCanonicalName());
        var responseType = elementUtils.getTypeElement(Response.class.getCanonicalName())
            .asType();
        futureType = types.erasure(futureElement.asType());
        defaultResponseType = types.getDeclaredType(futureElement, responseType);
    }

    /**
     * Generate an appropriate return statement for the type.
     *
     * @param returnType the type to return
     * @param builder    the method builder
     * @param function   the function on which to report errors
     */
    public void generateReturnStatement(
        TypeMirror returnType,
        MethodSpec.Builder builder,
        ExecutableElement function
    ) {
        // If no changes need to be made
        if (types.isSubtype(returnType, defaultResponseType)) {
            builder.addStatement("return response");
            return;
        }
        // If function returns future, but generic type is not Response
        if (types.isSameType(futureType, types.erasure(returnType))) {
            generateMappingSource(builder, returnType);
            return;
        }
        messager.printMessage(Diagnostic.Kind.ERROR, "No adapter to convert element from " + defaultResponseType + " to " + returnType, function);
    }

    private void generateMappingSource(MethodSpec.Builder builder, TypeMirror returnType) {
        var actualReturnType = ((DeclaredType) returnType).getTypeArguments().get(0);
        var conversionLambdaBuilder = CodeBlock.builder()
            .beginControlFlow("if (r.status() < 200 || r.status() >= 300)")
            .addStatement("throw new $T(r.status(), r.body())", RequestFailedException.class)
            .endControlFlow()
            .beginControlFlow("try");
        if (isSimpleType(actualReturnType)) {
            conversionLambdaBuilder.addStatement("return converter.deserialize(r, $T.class)", actualReturnType);
        } else {
            conversionLambdaBuilder.addStatement("var conversionTypeHolder = new $T<$T>(){}", GenericReference.class, actualReturnType)
                .addStatement("return converter.deserialize(r, conversionTypeHolder.getType())");
        }
        var conversionLambda = conversionLambdaBuilder.nextControlFlow("catch ($T exception)", IOException.class)
            .addStatement("throw new $T(exception)", CompletionException.class)
            .endControlFlow()
            .build();
        var conversionCaller = CodeBlock.builder()
            .beginControlFlow("return response.thenApply(r ->")
            .add(conversionLambda)
            .endControlFlow(")");
        builder.addCode(conversionCaller.build());
    }

    private boolean isSimpleType(TypeMirror returnType) {
        if (!(returnType instanceof DeclaredType declaredType)) {
            return true;
        }
        return declaredType.getTypeArguments().isEmpty();
    }
}
