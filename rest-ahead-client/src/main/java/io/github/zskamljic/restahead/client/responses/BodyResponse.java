package io.github.zskamljic.restahead.client.responses;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a response from the server where call will not fail for non 2xx codes.
 *
 * @param status    the response code of the request
 * @param headers   the headers present in the response
 * @param body      the body, if request was successful
 * @param errorBody the error body if request was not successful
 * @param <T>       the type of body to deserialize
 */
public record BodyResponse<T>(
    int status,
    Map<String, List<String>> headers,
    Optional<T> body,
    Optional<InputStream> errorBody
) {
}
