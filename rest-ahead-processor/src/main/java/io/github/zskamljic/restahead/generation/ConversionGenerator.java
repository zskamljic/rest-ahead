package io.github.zskamljic.restahead.generation;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import io.github.zskamljic.restahead.client.responses.BodyAndErrorResponse;
import io.github.zskamljic.restahead.client.responses.BodyResponse;
import io.github.zskamljic.restahead.conversion.GenericReference;
import io.github.zskamljic.restahead.conversion.OptionsConverter;
import io.github.zskamljic.restahead.exceptions.RequestFailedException;
import io.github.zskamljic.restahead.modeling.conversion.BodyAndErrorConversion;
import io.github.zskamljic.restahead.modeling.conversion.BodyResponseConversion;
import io.github.zskamljic.restahead.modeling.conversion.Conversion;
import io.github.zskamljic.restahead.modeling.conversion.DirectConversion;
import io.github.zskamljic.restahead.modeling.conversion.OptionsConversion;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

/**
 * Generates code for calling the converters.
 */
public class ConversionGenerator {

    /**
     * Generates the code used to map to another source
     *
     * @param builder    the builder to which the code should be added
     * @param conversion the conversion of the body to use
     */
    public void generateConversion(MethodSpec.Builder builder, Conversion conversion) {
        if (conversion instanceof DirectConversion directConversion) {
            generateDirectConversion(builder, directConversion.targetType());
        } else if (conversion instanceof BodyResponseConversion bodyResponseConversion) {
            generateBodyResponseConversion(builder, bodyResponseConversion.targetType());
        } else if (conversion instanceof BodyAndErrorConversion bodyAndErrorConversion) {
            generateBodyAndErrorConversion(builder, bodyAndErrorConversion.bodyType(), bodyAndErrorConversion.errorType());
        } else if (conversion instanceof OptionsConversion optionsConversion) {
            generateOptionsConversion(builder, optionsConversion.verbListType());
        }
    }

    /**
     * Generates the code for direct conversion, where no special handling is required.
     *
     * @param builder    the builder to which to add the code
     * @param returnType the type to deserialize to
     */
    private void generateDirectConversion(MethodSpec.Builder builder, TypeMirror returnType) {
        generateConversionCommon(
            "$T",
            new Object[]{returnType},
            conversionBuilder -> conversionBuilder.addStatement("throw new $T(r.status(), r.body())", RequestFailedException.class),
            conversionBuilder -> conversionBuilder.addStatement("return $L", Variables.DESERIALIZED),
            returnType,
            builder
        );
    }

    /**
     * Generates conversion code for {@link BodyResponse}.
     *
     * @param builder    the builder to which to add the code
     * @param targetType the type to deserialize to if response code was 2xx
     */
    private void generateBodyResponseConversion(MethodSpec.Builder builder, TypeMirror targetType) {
        generateConversionCommon(
            "$T<$T>",
            new Object[]{BodyResponse.class, targetType},
            conversionBuilder -> conversionBuilder.addStatement(
                "return new $T<$T>(r.status(), r.headers(), $T.empty(), $T.of(r.body()))",
                BodyResponse.class, targetType, Optional.class, Optional.class
            ),
            conversionBuilder -> conversionBuilder.addStatement(
                "return new $T<$T>(r.status(), r.headers(), $T.of($L), $T.empty())",
                BodyResponse.class, targetType, Optional.class, Variables.DESERIALIZED, Optional.class
            ),
            targetType,
            builder
        );
    }

    /**
     * Generates conversion code for {@link BodyAndErrorResponse}.
     *
     * @param builder   the builder to which to add the code
     * @param bodyType  the type to deserialize to if the request returned 2xx
     * @param errorType the type to deserialize to if the request did not return 2xx
     */
    private void generateBodyAndErrorConversion(MethodSpec.Builder builder, TypeMirror bodyType, TypeMirror errorType) {
        generateConversionCommon(
            "$T<$T, $T>",
            new Object[]{BodyAndErrorResponse.class, bodyType, errorType},
            conversionBuilder -> addDeserializationBlock(conversionBuilder, errorType, b -> b.addStatement(
                "return new $T<$T, $T>(r.status(), r.headers(), $T.empty(), $T.of($L))",
                BodyAndErrorResponse.class, bodyType, errorType, Optional.class, Optional.class, Variables.DESERIALIZED
            )),
            conversionBuilder -> conversionBuilder.addStatement(
                "return new $T<$T, $T>(r.status(), r.headers(), $T.of($L), $T.empty())",
                BodyAndErrorResponse.class, bodyType, errorType, Optional.class, Variables.DESERIALIZED, Optional.class
            ),
            bodyType,
            builder
        );
    }

    private void generateOptionsConversion(MethodSpec.Builder builder, TypeMirror typeMirror) {
        builder.addCode(CodeBlock.builder()
            .beginControlFlow("$T<$T> $L = $L.thenApply(r ->", CompletableFuture.class, typeMirror, Variables.CONVERTED_NAME, Variables.RESPONSE)
            .add("return $T.parseOptions($L);\n", OptionsConverter.class, "r")
            .endControlFlow(")")
            .build());
    }

    /**
     * Generate the common code for generating the conversion.
     *
     * @param responseType       the format for response type
     * @param responseTypeParams the extra parameters for generating the response type
     * @param addFailureCode     function that will generate return for non 2xx responses
     * @param addSuccessCode     function that will generate return for 2xx responses
     * @param returnType         the final return type for successful request
     * @param builder            the builder to add the code to
     */
    private void generateConversionCommon(
        String responseType,
        Object[] responseTypeParams,
        Consumer<CodeBlock.Builder> addFailureCode,
        Consumer<CodeBlock.Builder> addSuccessCode,
        TypeMirror returnType,
        MethodSpec.Builder builder
    ) {
        var conversionLambdaBuilder = CodeBlock.builder()
            .beginControlFlow("if (r.status() < 200 || r.status() >= 300)");
        addFailureCode.accept(conversionLambdaBuilder);
        conversionLambdaBuilder.endControlFlow();
        addDeserializationBlock(conversionLambdaBuilder, returnType, addSuccessCode);
        var allParameters = new Object[responseTypeParams.length + 3];
        allParameters[0] = CompletableFuture.class;
        System.arraycopy(responseTypeParams, 0, allParameters, 1, responseTypeParams.length);
        allParameters[responseTypeParams.length + 1] = Variables.CONVERTED_NAME;
        allParameters[responseTypeParams.length + 2] = Variables.RESPONSE;
        var conversionCaller = CodeBlock.builder()
            .beginControlFlow("$T<%s> $L = $L.thenApply(r ->".formatted(responseType), allParameters)
            .add(conversionLambdaBuilder.build())
            .endControlFlow(")");
        builder.addCode(conversionCaller.build());
    }

    /**
     * Add the deserialization block, try catch included.
     *
     * @param conversionLambdaBuilder the builder to which to add the code.
     * @param returnType              the type to deserialize to
     * @param addSuccessCode          function that will add code generation for successful deserialization
     */
    private void addDeserializationBlock(CodeBlock.Builder conversionLambdaBuilder, TypeMirror returnType, Consumer<CodeBlock.Builder> addSuccessCode) {
        conversionLambdaBuilder.beginControlFlow("try");

        if (isSimpleType(returnType)) {
            conversionLambdaBuilder.addStatement("$T $L = $L.deserialize(r, $T.class)", returnType, Variables.DESERIALIZED, Variables.CONVERTER, returnType);
        } else {
            conversionLambdaBuilder.addStatement("var $L = new $T<$T>(){}", Variables.CONVERSION_TYPE_HOLDER, GenericReference.class, returnType)
                .addStatement("$T $L = $L.deserialize(r, $L.getType())", returnType, Variables.DESERIALIZED, Variables.CONVERTER, Variables.CONVERSION_TYPE_HOLDER);
        }
        addSuccessCode.accept(conversionLambdaBuilder);

        conversionLambdaBuilder.nextControlFlow("catch ($T exception)", IOException.class)
            .addStatement("throw new $T(exception)", CompletionException.class)
            .endControlFlow();
    }

    /**
     * Whether the type is absent from generic parts
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
