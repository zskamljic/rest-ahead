package io.github.zskamljic.restahead.annotations.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that the method, when used in form body, should use the specified name.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface FormName {
    String value();
}
