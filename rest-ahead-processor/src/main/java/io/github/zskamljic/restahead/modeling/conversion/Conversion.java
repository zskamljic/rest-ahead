package io.github.zskamljic.restahead.modeling.conversion;

/**
 * Contains strategies to use for converting the request body.
 */
public sealed interface Conversion permits BodyAndErrorConversion, BodyResponseConversion, DirectConversion, OptionsConversion {
}
