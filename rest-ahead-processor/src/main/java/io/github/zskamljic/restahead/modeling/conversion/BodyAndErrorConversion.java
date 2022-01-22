package io.github.zskamljic.restahead.modeling.conversion;

import javax.lang.model.type.TypeMirror;

/**
 * Used to transfer both body and error body types to the code generator for deserialization.
 *
 * @param bodyType  the type to convert in case of success
 * @param errorType the type to convert in case of error
 */
public record BodyAndErrorConversion(TypeMirror bodyType, TypeMirror errorType) implements Conversion {
}
