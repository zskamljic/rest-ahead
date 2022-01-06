package com.lablizards.restahead.generation;

import com.lablizards.restahead.modeling.declaration.BodyDeclaration;
import com.lablizards.restahead.modeling.declaration.CallDeclaration;
import com.lablizards.restahead.modeling.declaration.ParameterDeclaration;
import com.lablizards.restahead.requests.request.RequestLine;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.List;

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

        builder.addStatement("var $L = $L.execute($L)", Variables.RESPONSE, Variables.CLIENT, Variables.REQUEST);
        responseGenerator.generateReturnStatement(call.returnDeclaration(), declaredExceptions, builder);
        return builder.build();
    }

    /**
     * Adds the body code request to the method.
     *
     * @param builder            the builder where the body code should be added
     * @param body               the body declaration with parameter to use
     * @param declaredExceptions the exceptions that the enclosing function already declares
     */
    private void addSendBodyStatement(
        MethodSpec.Builder builder,
        BodyDeclaration body,
        List<TypeMirror> declaredExceptions
    ) {
        exceptionsGenerator.generateTryCatchIfNeeded(
            builder,
            declaredExceptions,
            body.convertExceptions(),
            () -> builder.addStatement("$L.setBody($L.serialize($L))", Variables.REQUEST, Variables.CONVERTER, body.parameterName())
        );
    }

    /**
     * Add request initialization lines.
     *
     * @param builder     the method builder
     * @param requestLine the request line info
     * @param parameters  the request parameters
     */
    private void addRequestInitialization(
        MethodSpec.Builder builder,
        RequestLine requestLine,
        ParameterDeclaration parameters
    ) {
        builder.addStatement("var $L = new $T($S)", Variables.REQUEST, requestLine.request(), requestLine.path());

        for (var header : parameters.headers()) {
            if (header.isIterable()) {
                builder.beginControlFlow("for (var $L : $L)", Variables.HEADER_ITEM, header.codeName());
                builder.addStatement("$L.addHeader($S, $T.valueOf($L))", Variables.REQUEST, header.httpName(), String.class, Variables.HEADER_ITEM);
                builder.endControlFlow();
            } else {
                builder.addStatement(
                    "$L.addHeader($S, $T.valueOf($L))", Variables.REQUEST, header.httpName(), String.class, header.codeName()
                );
            }
        }
        for (var query : parameters.query()) {
            if (query.isIterable()) {
                builder.beginControlFlow("for (var $L : $L)", Variables.QUERY_ITEM, query.codeName());
                builder.addStatement("$L.addQuery($S, $T.valueOf($L))", Variables.REQUEST, query.httpName(), String.class, Variables.QUERY_ITEM);
                builder.endControlFlow();
            } else {
                builder.addStatement(
                    "$L.addQuery($S, $T.valueOf($L))", Variables.REQUEST, query.httpName(), String.class, query.codeName()
                );
            }
        }
        for (var query : parameters.presetQueries()) {
            builder.addStatement(
                "$L.addQuery($S, $S)", Variables.REQUEST, query.name(), query.value()
            );
        }
    }
}
