package io.github.zskamljic.restahead.client.responses;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a response from the server where call will not fail for non 2xx codes. Body will be deserialized in the errorBody.
 *
 * @param status    the response code of the request
 * @param headers   the headers present in the response
 * @param body      the body, if request was successful
 * @param errorBody the error body if request was not successful
 * @param <B>    the type of successful body
 * @param <E>   the type of error body (codes not in 2xx range)
 */
public record BodyAndErrorResponse<B, E>(
    int status,
    Map<String, List<String>> headers,
    Optional<B> body,
    Optional<E> errorBody
) {
}
