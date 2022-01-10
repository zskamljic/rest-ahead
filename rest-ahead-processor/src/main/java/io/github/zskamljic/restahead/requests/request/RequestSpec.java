package io.github.zskamljic.restahead.requests.request;

import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;

/**
 * Call specification
 *
 * @param requestLine the verb and path
 * @param parameters  the function parameters that need to be declared
 */
public record RequestSpec(
    RequestLine requestLine,
    ParameterDeclaration parameters
) {
}