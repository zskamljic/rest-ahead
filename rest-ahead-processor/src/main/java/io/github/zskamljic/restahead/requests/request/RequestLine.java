package io.github.zskamljic.restahead.requests.request;

import io.github.zskamljic.restahead.client.requests.PatchRequest;
import io.github.zskamljic.restahead.client.requests.PostRequest;
import io.github.zskamljic.restahead.client.requests.PutRequest;
import io.github.zskamljic.restahead.client.requests.Request;

/**
 * Contains the request verb and path.
 *
 * @param request the method for the request
 * @param path    the path for the request
 */
public record RequestLine(
    Class<? extends Request> request,
    String path
) {
    public boolean allowsBody() {
        return request == PatchRequest.class ||
            request == PostRequest.class ||
            request == PutRequest.class;
    }
}
