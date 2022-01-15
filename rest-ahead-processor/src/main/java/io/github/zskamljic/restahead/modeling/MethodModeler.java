package io.github.zskamljic.restahead.modeling;

import io.github.zskamljic.restahead.modeling.declaration.AdapterClassDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.CallDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.RequestParameterSpec;
import io.github.zskamljic.restahead.modeling.validation.PathValidator;
import io.github.zskamljic.restahead.requests.VerbMapping;
import io.github.zskamljic.restahead.requests.request.RequestLine;
import io.github.zskamljic.restahead.requests.request.path.TemplatedPath;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Used to generate models for methods from {@link ExecutableElement}.
 */
public class MethodModeler {
    private final Messager messager;
    private final ParameterModeler parameterModeler;
    private final PathValidator pathValidator;
    private final ReturnTypeModeler returnTypeModeler;

    public MethodModeler(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        pathValidator = new PathValidator(messager, elements, types);
        this.parameterModeler = new ParameterModeler(messager, elements, types, pathValidator);
        returnTypeModeler = new ReturnTypeModeler(messager, elements, types);
    }

    /**
     * Extract data from the function and validate it.
     *
     * @param function the function that the {@link CallDeclaration} should be created for
     * @param adapters the response adapters
     * @return empty if any errors were found, the declaration otherwise
     */
    public Optional<CallDeclaration> getCallDeclaration(
        ExecutableElement function,
        List<AdapterClassDeclaration> adapters
    ) {
        var presentAnnotations = VerbMapping.ANNOTATION_VERBS.stream()
            .map(function::getAnnotation)
            .filter(Objects::nonNull)
            .toList();

        if (presentAnnotations.size() != 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Exactly one verb annotation must be present on method", function);
            return Optional.empty();
        }

        var annotation = presentAnnotations.get(0);
        var requestLine = VerbMapping.annotationToVerb(annotation);

        var parameters = parameterModeler.getMethodParameters(function, requestLine.allowsBody());
        var updatedLine = pathValidator.validatePathAndExtractQuery(function, requestLine, parameters);
        if (updatedLine.isEmpty()) {
            return Optional.empty();
        }

        if (hasInvalidPathParameters(parameters.paths(), updatedLine.get(), function)) {
            return Optional.empty();
        }

        var exceptions = function.getThrownTypes()
            .stream()
            .map(TypeMirror.class::cast)
            .toList();

        return returnTypeModeler.getReturnConfiguration(function, adapters)
            .map(returnType -> new CallDeclaration(function, exceptions, updatedLine.get(), parameters, returnType));
    }

    /**
     * Checks if expected parameters and declared parameters match or not
     *
     * @param paths       the paths from function parameters
     * @param requestLine the path from annotation
     * @param function    the function on which to report errors
     * @return true if any errors are discovered, false otherwise.
     */
    private boolean hasInvalidPathParameters(
        List<RequestParameterSpec> paths,
        RequestLine requestLine,
        ExecutableElement function
    ) {
        var annotationPaths = paths.stream()
            .map(RequestParameterSpec::httpName)
            .collect(Collectors.toSet());
        if (annotationPaths.size() != paths.size()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Path parameters must be unique", function);
            return false;
        }

        if (!(requestLine.path() instanceof TemplatedPath templatedPath)) {
            if (annotationPaths.isEmpty()) return false;

            messager.printMessage(Diagnostic.Kind.ERROR, "Path parameters are present, but there are none expected", function);
            return true;
        }

        var requestLineParameters = new HashSet<>(templatedPath.getRequiredParameters());
        if (requestLineParameters.size() != templatedPath.getRequiredParameters().size()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Path contains duplicate arguments.", function);
            return true;
        }

        if (annotationPaths.size() == requestLineParameters.size() && annotationPaths.containsAll(requestLineParameters)) {
            return false;
        }

        var missingParameters = new HashSet<>(requestLineParameters);
        missingParameters.removeAll(annotationPaths);

        var missingPathDeclarations = new HashSet<>(annotationPaths);
        missingPathDeclarations.removeAll(requestLineParameters);
        messager.printMessage(
            Diagnostic.Kind.ERROR,
            "Expected and provided path variables do not match. Missing from parameters: %s, missing from path: %s"
                .formatted(
                    String.join(", ", missingParameters),
                    String.join(", ", missingPathDeclarations)),
            function
        );
        return true;
    }
}
