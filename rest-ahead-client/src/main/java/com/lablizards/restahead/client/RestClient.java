package com.lablizards.restahead.client;

import com.lablizards.restahead.client.requests.Request;

import java.util.concurrent.CompletableFuture;

/**
 * Instances of RestClient should handle {@link Request} instances and return raw responses.
 */
public interface RestClient {
    /**
     * Execute the specified request.
     *
     * @param request the request to perform
     * @return response from executing the request
     */
    CompletableFuture<Response> execute(Request request);
}
