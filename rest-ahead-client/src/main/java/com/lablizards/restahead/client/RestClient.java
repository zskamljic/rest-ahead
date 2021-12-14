package com.lablizards.restahead.client;

import com.lablizards.restahead.client.requests.Request;

import java.io.IOException;

/**
 * Instances of RestClient should handle {@link Request} instances and return raw responses.
 */
public interface RestClient {
    /**
     * Execute the specified request.
     *
     * @param request the request to perform
     * @return response from executing the request
     * @throws IOException          if an error occurred when executing the request
     * @throws InterruptedException if the thread executing the request was interrupted
     */
    Response execute(Request request) throws IOException, InterruptedException;
}
