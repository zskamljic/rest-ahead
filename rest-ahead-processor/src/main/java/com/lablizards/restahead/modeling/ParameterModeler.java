package com.lablizards.restahead.modeling;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.request.Query;
import com.lablizards.restahead.modeling.declaration.ParameterDeclaration;
import com.lablizards.restahead.modeling.declaration.RequestParameterSpec;
import com.lablizards.restahead.modeling.validation.HeaderValidator;
import com.lablizards.restahead.modeling.validation.QueryValidator;

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
 * Used to extract parameter info from the declaration.
 */
public class ParameterModeler {
    private static final List<Class<? extends Annotation>> EXPECTED_ANNOTATIONS = List.of(
        Header.class, Query.class
    );

    private final Messager messager;
    private final HeaderValidator headerValidator;
    private final QueryValidator queryValidator;

    public ParameterModeler(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        headerValidator = new HeaderValidator(messager, elements, types);
        queryValidator = new QueryValidator(messager, elements, types);
    }

    /**
     * Extracts the parameters, reporting errors if any parameter does not fit into the request.
     *
     * @param function the function from which to get the parameters
     */
    public ParameterDeclaration getMethodParameters(ExecutableElement function) {
        var parameters = function.getParameters();

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

        return new ParameterDeclaration(headers, queries);
    }

    /**
     * Generate and qualify parameter based on annotation
     *
     * @param parameter  the parameter to report errors on
     * @param annotation the annotation to qualify
     * @param headers    the list to collect headers in
     * @param queries    the list to collect queries in
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
