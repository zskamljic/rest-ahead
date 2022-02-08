package io.github.zskamljic.restahead.polyglot;

import io.github.zskamljic.restahead.annotations.Adapter;
import io.github.zskamljic.restahead.modeling.declaration.BodyParameter;
import io.github.zskamljic.restahead.modeling.parameters.ParameterWithExceptions;
import io.github.zskamljic.restahead.modeling.parameters.PartData;
import io.github.zskamljic.restahead.modeling.parameters.RequestParameter;
import io.github.zskamljic.restahead.request.BasicRequestLine;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
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
    private final List<Dialect> dialects = ServiceLoader.load(Dialect.class)
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
        return dialects.stream()
            .map(Dialect::verbAnnotations)
            .flatMap(Collection::stream);
    }

    /**
     * Returns all request annotations from discovered dialects.
     *
     * @return a stream of annotation classes
     */
    public Stream<Class<? extends Annotation>> requestAnnotations() {
        return dialects.stream()
            .map(Dialect::requestAnnotations)
            .flatMap(Collection::stream);
    }

    /**
     * Returns all body annotations from discovered dialects.
     *
     * @return a stream of annotation classes
     */
    public Stream<Class<? extends Annotation>> bodyAnnotations() {
        return dialects.stream()
            .map(Dialect::bodyAnnotations)
            .flatMap(Collection::stream);
    }

    /**
     * Attempts to create a basic request line for given annotation.
     *
     * @param annotation the annotation with required data
     * @return the request line based on data
     * @throws IllegalArgumentException if no valid {@link BasicRequestLine} could be created
     */
    public BasicRequestLine basicRequestLine(Annotation annotation) {
        return dialects.stream()
            .map(dialect -> dialect.getRequestLine(annotation))
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
        return dialects.stream()
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
                dialects.stream()
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
        return dialects.stream()
            .map(dialect -> dialect.getClass().getSimpleName())
            .collect(Collectors.joining(", "));
    }

    public Optional<RequestParameter> extractRequestAnnotation(Annotation annotation) {
        return dialects.stream()
            .map(dialect -> dialect.extractRequestAnnotation(annotation))
            .flatMap(Optional::stream)
            .findFirst();
    }

    public Optional<PartData> extractParts(List<? extends Annotation> bodyAnnotations) {
        return dialects.stream()
            .map(dialect -> dialect.extractPart(bodyAnnotations))
            .flatMap(Optional::stream)
            .findFirst();
    }

    public Optional<ParameterWithExceptions> createBodyPart(Elements elements, Types types, BodyParameter body, TypeMirror type) {
        return dialects.stream()
            .map(dialect -> dialect.createBodyPart(elements, types, body, type))
            .flatMap(Optional::stream)
            .findFirst();
    }
}
