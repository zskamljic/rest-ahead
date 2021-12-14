package com.lablizards.restahead.requests;

import com.lablizards.restahead.client.requests.Request;

/**
 * Call specification
 *
 * @param request the method for the request
 * @param path       the path for the request
 */
public record RequestSpec(
    Class<? extends Request> request,
    String path
) {
}
