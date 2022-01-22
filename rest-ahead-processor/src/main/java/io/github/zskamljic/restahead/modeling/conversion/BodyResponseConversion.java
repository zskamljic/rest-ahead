package io.github.zskamljic.restahead.modeling.conversion;

import javax.lang.model.type.TypeMirror;

/**
 * Used to transfer the body type to the code generator.
 *
 * @param targetType the type to convert the body if request is successful
 */
public record BodyResponseConversion(TypeMirror targetType) implements Conversion {
}
