package io.github.zskamljic.restahead.polyglot;

import io.github.zskamljic.restahead.modeling.declaration.BodyParameter;
import io.github.zskamljic.restahead.modeling.parameters.ParameterWithExceptions;
import io.github.zskamljic.restahead.modeling.parameters.PartData;
import io.github.zskamljic.restahead.modeling.parameters.RequestParameter;
import io.github.zskamljic.restahead.request.BasicRequestLine;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a dialect to use with RestAhead. If META-INF/services/io.github.zskamljic.restahead.polyglot.Dialect is
 * discovered with a valid subclass of Dialect then the processor will additionally use that dialect to generate the
 * source for all valid services.
 */
public interface Dialect {
    /**
     * Return a list of all the annotations supported by this class.
     *
     * @return the full list of annotations.
     */
    default List<Class<? extends Annotation>> allAnnotations() {
        var annotations = new ArrayList<>(requestAnnotations());
        annotations.addAll(bodyAnnotations());
        annotations.addAll(verbAnnotations());
        return annotations;
    }

    /**
     * Return a list of request annotations, such as Query, Header, Path etc.
     *
     * @return the list of request annotations.
     */
    List<Class<? extends Annotation>> requestAnnotations();

    /**
     * Return a list of body annotations, such as Body, FormName, Part etc.
     *
     * @return the list of body annotations.
     */
    List<Class<? extends Annotation>> bodyAnnotations();

    /**
     * Return a list of verb annotations, such as Delete, Get, Post etc.
     *
     * @return the verb annotations.
     */
    List<Class<? extends Annotation>> verbAnnotations();

    /**
     * Attempt to extract a {@link BasicRequestLine} from the annotation. Can be called with annotations from other
     * dialects. If no data can be extracted (unknown annotation, invalid data etc.) Optional.empty() can be returned.
     *
     * @param annotation the annotation to get the request line from
     * @return request line if annotation is valid and recognized, empty otherwise
     */
    Optional<BasicRequestLine> getRequestLine(Annotation annotation);

    /**
     * Attempts to extract a {@link RequestParameter} from the annotation. Can be called with annotations from other
     * dialects. If no data can be extracted (unknown annotation, invalid data etc.) Optional.empty() can be returned.
     *
     * @param annotation the annotation to get the parameter from
     * @return the parameter if parsed, empty otherwise
     */
    Optional<RequestParameter> extractRequestAnnotation(Annotation annotation);

    /**
     * Attempt to extract a {@link PartData} from the annotation. Can be called with annotations from other
     * dialects. If no data can be extracted (unknown annotation, invalid data etc.) Optional.empty() can be returned.
     *
     * @param bodyAnnotations body annotations present on the parameter
     * @return the extracted PartData if applicable
     */
    Optional<PartData> extractPart(List<? extends Annotation> bodyAnnotations);

    /**
     * Create a new body part if this dialect is familiar with the type.
     *
     * @param elements the elements utility to obtain references from
     * @param types    the types utility to help with type decision
     * @param body     the body information parsed from the parameter
     * @param type     the type of the parameter
     * @return full parameter info or empty
     */
    Optional<ParameterWithExceptions> createBodyPart(Elements elements, Types types, BodyParameter body, TypeMirror type);
}
