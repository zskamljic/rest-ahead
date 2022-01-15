package io.github.zskamljic.restahead.modeling;

import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.request.Path;
import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.modeling.declaration.BodyDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.RequestParameterSpec;
import io.github.zskamljic.restahead.modeling.validation.HeaderValidator;
import io.github.zskamljic.restahead.modeling.validation.PathValidator;
import io.github.zskamljic.restahead.modeling.validation.QueryValidator;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Used to extract parameter info from the declaration.
 */
public class ParameterModeler {
    private static final List<Class<? extends Annotation>> EXPECTED_ANNOTATIONS = List.of(
        Body.class, Header.class, Path.class, Query.class
    );

    private final Messager messager;
    private final PathValidator pathValidator;
    private final HeaderValidator headerValidator;
    private final QueryValidator queryValidator;
    private final TypeMirror ioException;

    public ParameterModeler(Messager messager, Elements elements, Types types, PathValidator pathValidator) {
        this.messager = messager;
        this.pathValidator = pathValidator;
        headerValidator = new HeaderValidator(messager, elements, types);
        queryValidator = new QueryValidator(messager, elements, types);
        ioException = elements.getTypeElement(IOException.class.getCanonicalName())
            .asType();
    }

    /**
     * Extracts the parameters, reporting errors if any parameter does not fit into the request.
     *
     * @param function   the function from which to get the parameters
     * @param allowsBody if body can be present in this request
     */
    public ParameterDeclaration getMethodParameters(ExecutableElement function, boolean allowsBody) {
        var parameters = function.getParameters();

        var headers = new ArrayList<RequestParameterSpec>();
        var queries = new ArrayList<RequestParameterSpec>();
        var paths = new ArrayList<RequestParameterSpec>();
        var bodies = new ArrayList<BodyDeclaration>();

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
            handleAnnotation(parameter, annotation, headers, queries, paths, bodies);
        }

        if (!bodies.isEmpty() && !allowsBody) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Body is not allowed for this type of request.", function);
            bodies.clear();
        }

        if (bodies.size() > 1) {
            bodies.forEach(
                body -> messager.printMessage(Diagnostic.Kind.ERROR, "Only one body is allowed per request.", body.element())
            );
        }
        var bodyDeclaration = bodies.stream()
            .findFirst();

        return new ParameterDeclaration(headers, queries, paths, bodyDeclaration);
    }

    /**
     * Generate and qualify parameter based on annotation
     *
     * @param parameter  the parameter to report errors on
     * @param annotation the annotation to qualify
     * @param headers    the list to collect headers in
     * @param queries    the list to collect queries in
     * @param paths      the paths to collect placeholders in
     * @param bodies     the list of bodies to collect bodies in
     */
    private void handleAnnotation(
        VariableElement parameter,
        Annotation annotation,
        List<RequestParameterSpec> headers,
        List<RequestParameterSpec> queries,
        List<RequestParameterSpec> paths,
        List<BodyDeclaration> bodies
    ) {
        if (annotation instanceof Header header) {
            headerValidator.getHeaderSpec(header.value(), parameter).ifPresent(headers::add);
        } else if (annotation instanceof Query query) {
            queryValidator.getQuerySpec(query.value(), parameter).ifPresent(queries::add);
        } else if (annotation instanceof Body) {
            bodies.add(new BodyDeclaration(parameter, parameter.getSimpleName().toString(), List.of(ioException)));
        } else if (annotation instanceof Path path) {
            pathValidator.getPathSpec(path.value(), parameter).ifPresent(paths::add);
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "Annotation is not supported here", parameter);
        }
    }
}
