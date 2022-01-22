package io.github.zskamljic.restahead.intercepting;

import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.client.requests.Request;

import java.util.concurrent.CompletableFuture;

/**
 * This interceptor with receive the request and apply some common logic on all calls.
 */
public interface Interceptor {
    /**
     * Intercepts the request, allowing modification of the request or response.
     *
     * @param chain   the chain used for proceeding through the chain
     * @param request the request that was intercepted
     * @return the response to return to the next element in the chain
     */
    CompletableFuture<Response> intercept(Chain chain, Request request);
}
