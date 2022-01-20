package io.github.zskamljic.restahead.requests.request;

import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;

/**
 * Call specification
 *
 * @param requestLine the verb and path
 * @param parameters  the function parts that need to be declared
 */
public record RequestSpec(
    BasicRequestLine requestLine,
    ParameterDeclaration parameters
) {
}
