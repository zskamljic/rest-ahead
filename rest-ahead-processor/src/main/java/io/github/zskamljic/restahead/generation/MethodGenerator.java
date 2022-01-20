package io.github.zskamljic.restahead.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import io.github.zskamljic.restahead.client.requests.MultiPartRequest;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.encoding.BodyEncoding;
import io.github.zskamljic.restahead.encoding.ConvertBodyEncoding;
import io.github.zskamljic.restahead.encoding.FormBodyEncoding;
import io.github.zskamljic.restahead.encoding.MultiPartBodyEncoding;
import io.github.zskamljic.restahead.modeling.declaration.CallDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.RequestParameterSpec;
import io.github.zskamljic.restahead.requests.request.RequestLine;
import io.github.zskamljic.restahead.requests.request.path.TemplatedPath;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to generate methods annotated with HTTP annotations.
 */
public class MethodGenerator {
    private final ExceptionsGenerator exceptionsGenerator = new ExceptionsGenerator();
    private final ResponseGenerator responseGenerator = new ResponseGenerator(exceptionsGenerator);

    /**
     * Generate methods for given declarations.
     *
     * @param calls the declarations with annotations for which to generate implementations.
     * @return the generated methods
     */
    public List<MethodSpec> generateMethods(List<CallDeclaration> calls) {
        return calls.stream()
            .map(this::generateMethodBody)
            .toList();
    }

    /**
     * Generate a method body.
     *
     * @param call the call for which the method is generated
     * @return the filled MethodSpec
     */
    private MethodSpec generateMethodBody(CallDeclaration call) {
        var builder = MethodSpec.overriding(call.function())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        var requestLine = call.requestLine();
        addRequestInitialization(builder, requestLine, call.parameters());

        var declaredExceptions = call.exceptions();
        call.parameters().body().ifPresent(body -> addSendBodyStatement(builder, body, declaredExceptions));

        builder.addStatement("var $L = $L.execute($L.build())", Variables.RESPONSE, Variables.CLIENT, Variables.REQUEST_BUILDER);
        responseGenerator.generateReturnStatement(call.returnDeclaration(), declaredExceptions, builder);
        return builder.build();
    }

    /**
     * Adds the body code request to the method.
     *
     * @param builder            the builder where the body code should be added
     * @param encoding           the body declaration with parameter(s) to use
     * @param declaredExceptions the exceptions that the enclosing function already declares
     */
    private void addSendBodyStatement(
        MethodSpec.Builder builder,
        BodyEncoding encoding,
        List<TypeMirror> declaredExceptions
    ) {
        if (encoding instanceof ConvertBodyEncoding convertEncoding) {
            addConvertEncoding(builder, declaredExceptions, convertEncoding.exceptions(), convertEncoding.parameterName());
        } else if (encoding instanceof FormBodyEncoding formEncoding) {
            addFormEncoding(builder, formEncoding.parameterName());
        } else if (encoding instanceof MultiPartBodyEncoding multipart) {
            addMultipartEncoding(builder, declaredExceptions, multipart);
        }
    }

    /**
     * Adds the default body encoding using the converter field.
     *
     * @param builder            the builder to which the code should be added
     * @param declaredExceptions the exceptions declared by the parent method
     * @param convertExceptions  the exceptions thrown by the converter
     * @param parameterName      the name of the parameter to convert
     */
    private void addConvertEncoding(
        MethodSpec.Builder builder,
        List<TypeMirror> declaredExceptions,
        List<TypeMirror> convertExceptions,
        String parameterName
    ) {
        exceptionsGenerator.generateTryCatchIfNeeded(
            builder,
            declaredExceptions,
            convertExceptions,
            () -> builder.addStatement("$L.setBody($L.serialize($L))", Variables.REQUEST_BUILDER, Variables.CONVERTER, parameterName)
        );
    }

    /**
     * Adds form encoding using the generated type and adds a Content-Type header for this body.
     *
     * @param builder       the builder to add the code to
     * @param parameterName the name of the parameter
     */
    private void addFormEncoding(MethodSpec.Builder builder, String parameterName) {
        var className = ClassName.get(MethodGenerator.class.getPackageName(), Variables.FORM_CONVERTER);
        builder.addStatement("$L.addHeader(\"Content-Type\", \"application/x-www-form-urlencoded\")", Variables.REQUEST_BUILDER)
            .addStatement(
                "$L.setBody($T.$L($L))",
                Variables.REQUEST_BUILDER, className, Variables.FORM_ENCODE, parameterName
            );
    }

    private void addMultipartEncoding(
        MethodSpec.Builder builder,
        List<TypeMirror> declaredExceptions,
        MultiPartBodyEncoding multipart
    ) {
        exceptionsGenerator.generateTryCatchIfNeeded(
            builder,
            declaredExceptions,
            multipart.exceptions(),
            () -> {
                builder.addCode("$T.builder()\n", MultiPartRequest.class);
                for (var part : multipart.parts()) {
                    part.type().ifPresentOrElse(
                        type -> builder.addCode(".addPart(new $T($S, $L))\n", type, part.httpName(), part.name()),
                        () -> builder.addCode(".addPart($L)\n", part.name())
                    );
                }
                builder.addStatement(".buildInto($L)", Variables.REQUEST_BUILDER);
            }
        );
    }

    /**
     * Add request initialization lines.
     *
     * @param builder     the method builder
     * @param requestLine the request line info
     * @param parameters  the request parts
     */
    private void addRequestInitialization(
        MethodSpec.Builder builder,
        RequestLine requestLine,
        ParameterDeclaration parameters
    ) {
        var requestBlock = CodeBlock.builder()
            .add("var $L = new $T()\n", Variables.REQUEST_BUILDER, Request.Builder.class)
            .add(".setVerb($T.$L)\n", Verb.class, requestLine.verb())
            .add(".setBaseUrl($L)\n", Variables.BASE_URL);
        if (requestLine.path() instanceof TemplatedPath path) {
            addTemplatedPath(requestBlock, path, parameters.paths());
        } else {
            requestBlock.add(".setPath($S)", requestLine.path());
        }
        builder.addStatement(requestBlock.build());

        for (var header : parameters.headers()) {
            if (header.isIterable()) {
                builder.beginControlFlow("for (var $L : $L)", Variables.HEADER_ITEM, header.codeName());
                builder.addStatement("$L.addHeader($S, $T.valueOf($L))", Variables.REQUEST_BUILDER, header.httpName(), String.class, Variables.HEADER_ITEM);
                builder.endControlFlow();
            } else {
                builder.addStatement(
                    "$L.addHeader($S, $T.valueOf($L))", Variables.REQUEST_BUILDER, header.httpName(), String.class, header.codeName()
                );
            }
        }
        for (var query : parameters.query()) {
            if (query.isIterable()) {
                builder.beginControlFlow("for (var $L : $L)", Variables.QUERY_ITEM, query.codeName());
                builder.addStatement("$L.addQuery($S, $T.valueOf($L))", Variables.REQUEST_BUILDER, query.httpName(), String.class, Variables.QUERY_ITEM);
                builder.endControlFlow();
            } else {
                builder.addStatement(
                    "$L.addQuery($S, $T.valueOf($L))", Variables.REQUEST_BUILDER, query.httpName(), String.class, query.codeName()
                );
            }
        }
        for (var query : parameters.presetQueries()) {
            builder.addStatement(
                "$L.addQuery($S, $S)", Variables.REQUEST_BUILDER, query.name(), query.value()
            );
        }
    }

    /**
     * Add path that contains parts.
     *
     * @param requestBlock the block to which to add the path code
     * @param path         the path definition
     * @param paths        the provided paths
     */
    private void addTemplatedPath(
        CodeBlock.Builder requestBlock,
        TemplatedPath path,
        List<RequestParameterSpec> paths
    ) {
        var pathToVariable = paths.stream()
            .collect(Collectors.toMap(RequestParameterSpec::httpName, RequestParameterSpec::httpName));

        var replaceBlocks = CodeBlock.builder()
            .add(".setPath($S", path);

        var requiredParams = path.getRequiredParameters();
        for (int i = 0; i < requiredParams.size(); i++) {
            var param = requiredParams.get(i);

            replaceBlocks.add(".replace($S, $T.valueOf($L))" + (i != requiredParams.size() - 1 ? "\n" : ""),
                "{%s}".formatted(param),
                String.class,
                pathToVariable.get(param));
        }

        replaceBlocks.add(")");

        requestBlock.add(replaceBlocks.build());
    }
}
