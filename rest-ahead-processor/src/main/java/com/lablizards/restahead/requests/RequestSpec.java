package com.lablizards.restahead.requests;

/**
 * Call specification
 * @param parameters the function parameters that need to be declared
 */
public record RequestSpec(
    RequestLine requestLine,
    RequestParameters parameters
) {
}
