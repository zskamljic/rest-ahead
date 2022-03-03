package io.github.zskamljic.restahead.polyglot;

import io.github.zskamljic.restahead.annotations.Adapter;
import io.github.zskamljic.restahead.modeling.declaration.BodyParameter;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.parameters.ParameterWithExceptions;
import io.github.zskamljic.restahead.modeling.parameters.PartData;
import io.github.zskamljic.restahead.modeling.parameters.RequestParameter;
import io.github.zskamljic.restahead.processor.RequestsProcessor;
import io.github.zskamljic.restahead.request.BasicRequestLine;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class that handles all dialects that are discovered by {@link ServiceLoader}.
 */
public class Dialects {
    private final List<Dialect> availableDialects = ServiceLoader.load(Dialect.class, RequestsProcessor.class.getClassLoader())
        .stream()
        .map(ServiceLoader.Provider::get)
        .toList();

    public Dialects(Messager messager) {
        messager.printMessage(Diagnostic.Kind.NOTE, "Detected dialects: " + createNameString());
    }

    /**
     * Returns all verb annotations from discovered dialects.
     *
     * @return a stream of annotation classes
     */
    public Stream<Class<? extends Annotation>> verbAnnotations() {
        return availableDialects.stream()
            .map(Dialect::verbAnnotations)
            .flatMap(Collection::stream);
    }

    /**
     * Returns all parameter annotations from discovered dialects.
     *
     * @return a stream of annotation classes
     */
    public Stream<Class<? extends Annotation>> parameterAnnotations() {
        return availableDialects.stream()
            .map(Dialect::parameterAnnotations)
            .flatMap(Collection::stream);
    }

    /**
     * Returns all body annotations from discovered dialects.
     *
     * @return a stream of annotation classes
     */
    public Stream<Class<? extends Annotation>> bodyAnnotations() {
        return availableDialects.stream()
            .map(Dialect::bodyAnnotations)
            .flatMap(Collection::stream);
    }

    /**
     * Attempts to create a basic request line for given annotation.
     *
     * @param function   the function on which the annotation is present
     * @param annotation the annotation with required data
     * @return the request line based on data
     * @throws IllegalArgumentException if no valid {@link BasicRequestLine} could be created
     */
    public BasicRequestLine basicRequestLine(ExecutableElement function, Annotation annotation) {
        return availableDialects.stream()
            .map(dialect -> dialect.getRequestLine(function, annotation))
            .flatMap(Optional::stream)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Annotation was not a valid verb: " + annotation));
    }

    /**
     * Checks if the element provided is a verb annotation.
     *
     * @param typeElement the element to check
     * @return true if the element is a verb, false otherwise
     */
    public boolean isVerbAnnotation(TypeElement typeElement) {
        var name = typeElement.getSimpleName().toString();
        return availableDialects.stream()
            .map(Dialect::verbAnnotations)
            .flatMap(Collection::stream)
            .map(Class::getSimpleName)
            .anyMatch(name::equals);
    }

    /**
     * Get a set of all supported annotations in string form.
     *
     * @return the full set of annotations
     */
    public Set<String> supportedAnnotationTypes() {
        return Stream.concat(
                availableDialects.stream()
                    .map(Dialect::allAnnotations)
                    .flatMap(List::stream),
                Stream.of(Adapter.class)
            )
            .map(Class::getCanonicalName)
            .collect(Collectors.toSet());
    }

    /**
     * Formats all the dialects by name and returns a comma separated list.
     *
     * @return the comma separated list of Dialect names
     */
    private String createNameString() {
        return availableDialects.stream()
            .map(dialect -> dialect.getClass().getSimpleName())
            .collect(Collectors.joining(", "));
    }

    /**
     * Get request annotation from annotation (GET, POST etc.)
     *
     * @param annotation the annotation
     * @return request or empty
     */
    public Optional<RequestParameter> extractRequestAnnotation(Annotation annotation) {
        return availableDialects.stream()
            .map(dialect -> dialect.extractParameterAnnotation(annotation))
            .flatMap(Optional::stream)
            .findFirst();
    }

    /**
     * Extract parts of body from annotation
     *
     * @param bodyAnnotations the annotations
     * @return part info or empty
     */
    public Optional<PartData> extractParts(List<? extends Annotation> bodyAnnotations) {
        return availableDialects.stream()
            .map(dialect -> dialect.extractPart(bodyAnnotations))
            .flatMap(Optional::stream)
            .findFirst();
    }

    /**
     * Delegate body part creation to all detected dialect, returning first valid one
     *
     * @param elements the elements to get type info from
     * @param types    the types utility
     * @param body     the body to process
     * @param type     the type of the body
     * @return first processed body or empty if no dialects return a valid value
     */
    public Optional<ParameterWithExceptions> createBodyPart(Elements elements, Types types, BodyParameter body, TypeMirror type) {
        return availableDialects.stream()
            .map(dialect -> dialect.createBodyPart(elements, types, body, type))
            .flatMap(Optional::stream)
            .findFirst();
    }

    /**
     * Process the annotations present on the request.
     *
     * @param function   the function to process
     * @param parameters the parameters to update if needed
     */
    public void handleRequestAnnotation(ExecutableElement function, ParameterDeclaration parameters) throws CompositeProcessingException {
        var errors = new ArrayList<ProcessingException>();
        for (var dialect : availableDialects) {
            try {
                dialect.processRequestAnnotations(function, parameters);
            } catch (ProcessingException e) {
                errors.add(e);
            }
        }
        if (!errors.isEmpty()) {
            throw new CompositeProcessingException(errors);
        }
    }

    /**
     * Checks if any of the annotations is a form body annotation
     *
     * @param bodyAnnotations the annotations to check
     */
    public boolean hasFormAnnotation(List<? extends Annotation> bodyAnnotations) {
        return availableDialects.stream()
            .anyMatch(dialect -> dialect.hasFormAnnotation(bodyAnnotations));
    }
}
