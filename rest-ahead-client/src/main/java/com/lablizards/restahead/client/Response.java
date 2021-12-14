package com.lablizards.restahead.client;

import java.io.InputStream;

/**
 * Response of an HTTP request.
 *
 * @param status the status code of the request
 * @param body   the response body
 */
public record Response(
    int status,
    InputStream body
) {
}
