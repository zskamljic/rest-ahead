package io.github.zskamljic.restahead.demo.interceptors;

import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.intercepting.Chain;
import io.github.zskamljic.restahead.intercepting.Interceptor;

import java.util.concurrent.CompletableFuture;

public class PreRequestInterceptor implements Interceptor {
    @Override
    public CompletableFuture<Response> intercept(Chain chain, Request request) {
        var newRequest = request.buildUpon()
            .setPath("get")
            .build();
        return chain.proceed(newRequest);
    }
}
