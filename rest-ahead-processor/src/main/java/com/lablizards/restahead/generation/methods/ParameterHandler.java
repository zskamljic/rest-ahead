package com.lablizards.restahead.generation.methods;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.request.Query;
import com.lablizards.restahead.requests.RequestParameters;
import com.lablizards.restahead.requests.parameters.RequestParameter;
import com.lablizards.restahead.requests.parameters.RequestParameterSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Used to perform parameter specific logic
 */
public class ParameterHandler {
    private static final List<Class<? extends Annotation>> EXPECTED_ANNOTATIONS = List.of(
        Header.class, Query.class
    );

    private final Messager messager;
    private final HeaderValidator headerValidator;
    private final QueryValidator queryValidator;

    /**
     * Create a new instance.
     *
     * @param messager the messager where errors are reported
     * @param elementUtils the elements used to lookup class info
     * @param types an instance of types utility
     */
    public ParameterHandler(Messager messager, Elements elementUtils, Types types) {
        this.messager = messager;
        headerValidator = new HeaderValidator(messager, elementUtils, types);
        queryValidator = new QueryValidator(messager, elementUtils, types);
    }

    /**
     * Creates {@link ParameterSpec} for the given parameter
     *
     * @param parameter the parameter to get the name and type from
     * @return the spec for parameter
     */
    public ParameterSpec createParameter(RequestParameter parameter) {
        return ParameterSpec.builder(TypeName.get(parameter.type()), parameter.name())
            .build();
    }

    /**
     * Extracts the parameters, reporting errors if any parameter does not fit into the request.
     *
     * @param function the function from which to get the parameters
     */
    public RequestParameters getMethodParameters(ExecutableElement function) {
        var parameters = function.getParameters();

        var allParameters = parameters.stream()
            .map(parameter -> new RequestParameter(parameter.asType(), parameter.getSimpleName().toString()))
            .toList();

        var headers = new ArrayList<RequestParameterSpec>();
        var queries = new ArrayList<RequestParameterSpec>();
        for (var parameter : parameters) {
            var presentAnnotations = EXPECTED_ANNOTATIONS.stream()
                .map(parameter::getAnnotation)
                .filter(Objects::nonNull)
                .toList();
            if (presentAnnotations.size() != 1) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Exactly one annotation expected on request parameter", parameter);
                continue;
            }
            var annotation = presentAnnotations.get(0);
            handleAnnotation(parameter, annotation, headers, queries);
        }

        return new RequestParameters(allParameters, headers, queries);
    }

    /**
     * Generate and qualify parameter based on annotation
     * @param parameter the parameter to report errors on
     * @param annotation the annotation to qualify
     * @param headers the list to collect headers in
     * @param queries the list to collect queries in
     */
    private void handleAnnotation(
        VariableElement parameter,
        Annotation annotation,
        List<RequestParameterSpec> headers,
        List<RequestParameterSpec> queries
    ) {
        if (annotation instanceof Header header) {
            headerValidator.getHeaderSpec(header.value(), parameter).ifPresent(headers::add);
        } else if (annotation instanceof Query query) {
            queryValidator.getQuerySpec(query.value(), parameter).ifPresent(queries::add);
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "Annotation is not supported here", parameter);
        }
    }
}
