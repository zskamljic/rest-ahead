package io.github.zskamljic.restahead.modeling.conversion;

import javax.lang.model.type.TypeMirror;

/**
 * Used for converting the value directly, where no special handling is required.
 *
 * @param targetType the type to convert to.
 */
public record DirectConversion(TypeMirror targetType) implements Conversion {
}
