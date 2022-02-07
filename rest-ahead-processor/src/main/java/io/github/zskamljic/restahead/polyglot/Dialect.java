package io.github.zskamljic.restahead.polyglot;

import io.github.zskamljic.restahead.request.BasicRequestLine;

import java.lang.annotation.Annotation;
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
    List<Class<? extends Annotation>> allAnnotations();

    /**
     * Return a list of utility annotations, such as Query, Header, Body etc.
     *
     * @return the list of utility annotations.
     */
    List<Class<? extends Annotation>> utilityAnnotations();

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
}
