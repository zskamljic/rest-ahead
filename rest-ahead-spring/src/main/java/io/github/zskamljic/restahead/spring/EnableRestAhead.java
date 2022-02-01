package io.github.zskamljic.restahead.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Scans for interfaces that have {@link RestAheadService} annotation and instantiates them as a bean.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RestAheadRegistrar.class)
public @interface EnableRestAhead {
}
