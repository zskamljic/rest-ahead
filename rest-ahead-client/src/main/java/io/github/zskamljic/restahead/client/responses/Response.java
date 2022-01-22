package io.github.zskamljic.restahead.client.responses;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Response of an HTTP request.
 *
 * @param status  the status code of the request
 * @param headers the response headers
 * @param body    the response body
 */
public record Response(
    int status,
    Map<String, List<String>> headers,
    InputStream body
) {
}
