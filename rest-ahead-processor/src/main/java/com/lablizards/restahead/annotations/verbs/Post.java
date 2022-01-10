package com.lablizards.restahead.annotations.verbs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Perform a POST request on path specified by param or "/" if not set
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Post {
    String value() default "";
}
