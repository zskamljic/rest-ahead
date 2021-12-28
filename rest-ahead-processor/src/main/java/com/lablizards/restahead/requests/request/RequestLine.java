package com.lablizards.restahead.requests.request;

import com.lablizards.restahead.client.requests.Request;

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
}
