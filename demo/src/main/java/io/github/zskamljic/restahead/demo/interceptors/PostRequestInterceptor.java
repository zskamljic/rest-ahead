package io.github.zskamljic.restahead.demo.interceptors;

import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.intercepting.Chain;
import io.github.zskamljic.restahead.intercepting.Interceptor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class PostRequestInterceptor implements Interceptor {
    private final AtomicReference<Response> response = new AtomicReference<>();

    @Override
    public CompletableFuture<Response> intercept(Chain chain, Request request) {
        var originalResponse = chain.proceed(request);
        return originalResponse.thenApply(value -> {
            response.set(value);
            return value;
        });
    }

    public Response getResponse() {
        return response.get();
    }
}
