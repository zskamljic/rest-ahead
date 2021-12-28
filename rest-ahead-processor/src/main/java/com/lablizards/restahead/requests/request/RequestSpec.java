package com.lablizards.restahead.requests.request;

import com.lablizards.restahead.requests.RequestParameters;
import com.lablizards.restahead.requests.request.RequestLine;

/**
 * Call specification
 * @param parameters the function parameters that need to be declared
 */
public record RequestSpec(
    RequestLine requestLine,
    RequestParameters parameters
) {
}
