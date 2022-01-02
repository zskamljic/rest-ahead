package com.lablizards.restahead.modeling.declaration;

/**
 * Contains the header information.
 *
 * @param httpName   the name of the parameter in HTTP request
 * @param codeName   the name of the parameter in code, to be passed to the request
 * @param isIterable whether the parameter is iterable (needs to be used in a loop)
 */
public record RequestParameterSpec(
    String httpName,
    String codeName,
    boolean isIterable
) {
}
