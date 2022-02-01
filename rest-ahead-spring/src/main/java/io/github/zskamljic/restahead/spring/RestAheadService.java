package io.github.zskamljic.restahead.spring;

import io.github.zskamljic.restahead.conversion.Converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for declaring that annotated interface is a RestAhead compatible service. The service will be made into
 * an injectable bean.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RestAheadService {
    /**
     * @return the base url of the service when it's instantiated
     */
    String url();

    /**
     * Converter will be automatically instantiated. This requires the provided class to have a public,
     * no-args constructor.
     *
     * @return the Converter class to use with this service. Can be left empty if no converter is required.
     */
    Class<? extends Converter> converter() default Converter.class;
}
