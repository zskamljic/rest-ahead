package io.github.zskamljic.restahead.client;

import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.intercepting.Chain;
import io.github.zskamljic.restahead.intercepting.Interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Instances of Client should handle {@link Request} instances and return raw responses.
 */
public abstract class Client {
    private final List<Interceptor> interceptors = new ArrayList<>();

    /**
     * Adds a new interceptor to the list.
     *
     * @param interceptor the interceptor to add
     */
    public void addInterceptor(Interceptor interceptor) {
        Objects.requireNonNull(interceptor);
        this.interceptors.add(interceptor);
    }

    /**
     * Execute the specified request, passing values through the interceptors.
     *
     * @param request the request to execute
     * @return the result from interceptors and internal client implementation
     */
    public CompletableFuture<Response> execute(Request request) {
        return new Chain(interceptors, this::performRequest).proceed(request);
    }

    /**
     * Execute the specified request with internal implementation.
     *
     * @param request the request to perform
     * @return response from executing the request
     */
    protected abstract CompletableFuture<Response> performRequest(Request request);
}
