package io.github.zskamljic.restahead.modeling.parameters;

import io.github.zskamljic.restahead.encoding.MultiPartParameter;

import javax.lang.model.type.TypeMirror;
import java.util.Set;

/**
 * Represents a parameter with exceptions that can be thrown when it's used
 *
 * @param parameter  the parameter that is added
 * @param exceptions the exceptions that can be thrown
 */
public record ParameterWithExceptions(
    MultiPartParameter parameter,
    Set<TypeMirror> exceptions
) {
}
