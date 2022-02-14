package io.github.zskamljic.restahead.encoding;

import io.github.zskamljic.restahead.client.requests.parts.MultiPart;

import java.util.Optional;

/**
 * A parameter that specifies a part of multipart request.
 *
 * @param httpName        the name to use in http transport
 * @param name            the name of function parameter
 * @param type            which type to use when generating the code, empty if it's already an appropriate type
 * @param extraParameters extra parameters that should be sent to the constructor
 */
public record MultiPartParameter(
    String httpName,
    String name,
    Optional<Class<? extends MultiPart>> type,
    Optional<String> extraParameters
) {
}
