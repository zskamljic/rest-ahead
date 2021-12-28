package com.lablizards.restahead.requests.request;

import com.lablizards.restahead.requests.RequestParameters;

/**
 * Call specification
 *
 * @param requestLine the verb and path
 * @param parameters  the function parameters that need to be declared
 */
public record RequestSpec(
    RequestLine requestLine,
    RequestParameters parameters
) {
}
