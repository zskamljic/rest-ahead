package io.github.zskamljic.restahead.modeling;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.modeling.conversion.BodyResponseConversion;
import io.github.zskamljic.restahead.modeling.declaration.AdapterClassDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.CallDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.RequestParameterSpec;
import io.github.zskamljic.restahead.modeling.declaration.ReturnDeclaration;
import io.github.zskamljic.restahead.modeling.validation.PathValidator;
import io.github.zskamljic.restahead.polyglot.CompositeProcessingException;
import io.github.zskamljic.restahead.polyglot.Dialects;
import io.github.zskamljic.restahead.request.RequestLine;
import io.github.zskamljic.restahead.request.path.TemplatedPath;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
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
    private final Dialects dialects;
    private final Types types;

    public MethodModeler(Messager messager, Elements elements, Types types, Dialects dialects) {
        this.messager = messager;
        this.dialects = dialects;
        this.types = types;
        pathValidator = new PathValidator(messager, elements, types);
        parameterModeler = new ParameterModeler(messager, elements, dialects, types, pathValidator);
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
        var presentAnnotations = dialects.verbAnnotations()
            .map(function::getAnnotation)
            .filter(Objects::nonNull)
            .toList();

        if (presentAnnotations.size() != 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Exactly one verb annotation must be present on method", function);
            return Optional.empty();
        }

        var annotation = presentAnnotations.get(0);
        var requestLine = dialects.basicRequestLine(function, annotation);

        var parameters = parameterModeler.getMethodParameters(function, requestLine.allowsBody());
        var updatedLine = pathValidator.extractRequestData(function, requestLine, parameters);
        if (updatedLine.isEmpty()) {
            return Optional.empty();
        }

        if (!extractRequestAnnotationData(function, parameters)) {
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
            .filter(type -> hasValidReturnType(updatedLine.get().verb(), type, function))
            .map(returnType -> new CallDeclaration(function, exceptions, updatedLine.get(), parameters, returnType));
    }

    /**
     * Attempt to extract request related parameters from the function.
     *
     * @param function   the function to extract parameters from
     * @param parameters the target for storing parameters
     * @return true for success, false for failure
     */
    private boolean extractRequestAnnotationData(ExecutableElement function, ParameterDeclaration parameters) {
        try {
            dialects.handleRequestAnnotation(function, parameters);
            return true;
        } catch (CompositeProcessingException e) {
            var exceptions = e.getExceptions();
            for (var exception : exceptions) {
                exception.report(messager);
            }
            return false;
        }
    }

    /**
     * Checks if the discovered return type is allowed for requests (for example, HEAD should not have a body)
     *
     * @param verb                the verb used in the request
     * @param returnConfiguration the discovered return type
     * @param function            the function that the error should be reported on
     * @return whether the return type is valid for request
     */
    private boolean hasValidReturnType(Verb verb, ReturnDeclaration returnConfiguration, ExecutableElement function) {
        if (verb != Verb.HEAD) return true;

        var adapterCall = returnConfiguration.adapterCall();
        var conversion = returnConfiguration.targetConversion();

        var isVoidAdapter = adapterCall.isPresent() && adapterCall.get().adapterMethod().returnType().getKind() == TypeKind.VOID;
        var isVoidBodyResponse = conversion.isPresent() &&
            conversion.get() instanceof BodyResponseConversion body &&
            Void.class.getCanonicalName().equals(body.targetType().toString());
        if (isVoidAdapter || isVoidBodyResponse) {
            return true;
        }

        messager.printMessage(Diagnostic.Kind.ERROR, "HEAD request responses should be of type BodyResponse<Void> or void", function);
        return false;
    }

    /**
     * Checks if expected parts and declared parts match or not
     *
     * @param paths       the paths from function parts
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
            messager.printMessage(Diagnostic.Kind.ERROR, "Path parts must be unique", function);
            return false;
        }

        if (!(requestLine.path() instanceof TemplatedPath templatedPath)) {
            if (annotationPaths.isEmpty()) return false;

            messager.printMessage(Diagnostic.Kind.ERROR, "Path parts are present, but there are none expected", function);
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
            "Expected and provided path variables do not match. Missing from parts: %s, missing from path: %s"
                .formatted(
                    String.join(", ", missingParameters),
                    String.join(", ", missingPathDeclarations)),
            function
        );
        return true;
    }
}
