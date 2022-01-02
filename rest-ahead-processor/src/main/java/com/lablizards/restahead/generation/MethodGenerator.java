package com.lablizards.restahead.generation;

import com.lablizards.restahead.exceptions.RestException;
import com.lablizards.restahead.modeling.declaration.CallDeclaration;
import com.lablizards.restahead.modeling.declaration.ParameterDeclaration;
import com.lablizards.restahead.requests.request.RequestLine;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Used to generate methods annotated with HTTP annotations.
 */
public class MethodGenerator {
    private final ResponseGenerator responseGenerator = new ResponseGenerator();

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

        var returnType = call.function().getReturnType();
        if (returnType.getKind() == TypeKind.VOID) {
            builder.beginControlFlow("try")
                .addStatement("client.execute(httpRequest).get()")
                .nextControlFlow("catch ($T exception)", ExecutionException.class)
                .addStatement("throw new $T(exception.getCause())", RestException.class)
                .nextControlFlow("catch ($T exception)", InterruptedException.class)
                .addStatement("throw new $T(exception)", RestException.class)
                .endControlFlow();
        } else {
            builder.addStatement("var response = client.execute(httpRequest)");
            responseGenerator.generateReturnStatement(call.returnDeclaration(), builder);
        }
        return builder.returns(TypeName.get(returnType))
            .build();
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
        builder.addStatement("var httpRequest = new $T($S)", requestLine.request(), requestLine.path());

        for (var header : parameters.headers()) {
            if (header.isIterable()) {
                builder.beginControlFlow("for (var headerItem : $L)", header.codeName());
                builder.addStatement("httpRequest.addHeader($S, $T.valueOf(headerItem))", header.httpName(), String.class);
                builder.endControlFlow();
            } else {
                builder.addStatement(
                    "httpRequest.addHeader($S, $T.valueOf($L))", header.httpName(), String.class, header.codeName()
                );
            }
        }
        for (var query : parameters.query()) {
            if (query.isIterable()) {
                builder.beginControlFlow("for (var headerItem : $L)", query.codeName());
                builder.addStatement("httpRequest.addQuery($S, $T.valueOf(headerItem))", query.httpName(), String.class);
                builder.endControlFlow();
            } else {
                builder.addStatement(
                    "httpRequest.addQuery($S, $T.valueOf($L))", query.httpName(), String.class, query.codeName()
                );
            }
        }
        for (var query : parameters.presetQueries()) {
            builder.addStatement(
                "httpRequest.addQuery($S, $S)", query.name(), query.value()
            );
        }
    }
}
