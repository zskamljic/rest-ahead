package com.lablizards.restahead.requests.parameters;

/**
 * Contains the header information.
 *
 * @param headerName    the name of the header (Accept, Content-Type etc.)
 * @param parameterName the name of the parameter, to be passed to the request
 */
public record HeaderSpec(
    String headerName,
    String parameterName,
    boolean isIterable
) {
}
