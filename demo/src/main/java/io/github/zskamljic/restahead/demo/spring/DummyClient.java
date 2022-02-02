package io.github.zskamljic.restahead.demo.spring;

import io.github.zskamljic.restahead.client.Client;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.intercepting.Interceptor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DummyClient extends Client {
    private final List<Interceptor> interceptors = new ArrayList<>();
    protected static final List<ClientRequestPair> requests = new ArrayList<>();

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    @Override
    public void addInterceptor(Interceptor interceptor) {
        super.addInterceptor(interceptor);
        interceptors.add(interceptor);
    }

    @Override
    protected CompletableFuture<Response> performRequest(Request request) {
        requests.add(new ClientRequestPair(this, request));
        return CompletableFuture.completedFuture(new Response(200, Map.of(), InputStream.nullInputStream()));
    }

    record ClientRequestPair(Client client, Request request) {
    }
}
