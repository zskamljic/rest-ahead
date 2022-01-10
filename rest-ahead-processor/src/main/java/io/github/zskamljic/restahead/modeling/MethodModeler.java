package io.github.zskamljic.restahead.modeling;

import io.github.zskamljic.restahead.modeling.declaration.AdapterClassDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.CallDeclaration;
import io.github.zskamljic.restahead.modeling.validation.PathValidator;
import io.github.zskamljic.restahead.requests.VerbMapping;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        this.parameterModeler = new ParameterModeler(messager, elements, types);
        pathValidator = new PathValidator(messager);
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

        var exceptions = function.getThrownTypes()
            .stream()
            .map(TypeMirror.class::cast)
            .toList();

        return returnTypeModeler.getReturnConfiguration(function, adapters)
            .map(returnType -> new CallDeclaration(function, exceptions, updatedLine.get(), parameters, returnType));
    }
}
