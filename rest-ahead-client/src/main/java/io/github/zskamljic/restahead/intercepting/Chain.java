package io.github.zskamljic.restahead.intercepting;

import io.github.zskamljic.restahead.client.Response;
import io.github.zskamljic.restahead.client.requests.Request;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Represents a chain through which the request passes on the way to response.
 */
public class Chain {
    private final List<Interceptor> interceptors;
    private final Function<Request, CompletableFuture<Response>> exchangeFunction;
    private int currentInterceptor;

    public Chain(
        List<Interceptor> interceptors,
        Function<Request, CompletableFuture<Response>> exchangeFunction
    ) {
        this.interceptors = interceptors;
        this.exchangeFunction = exchangeFunction;
    }

    /**
     * Proceeds with the request, using the next interceptor in the chain. If no more interceptors are left call the client.
     *
     * @param request the request to use
     * @return the response obtained from client
     */
    public CompletableFuture<Response> proceed(Request request) {
        if (currentInterceptor == interceptors.size()) {
            return exchangeFunction.apply(request);
        }
        var current = currentInterceptor;
        currentInterceptor++;
        return interceptors.get(current).intercept(this, request);
    }
}
