package io.github.zskamljic.restahead.demo.spring;

import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.demo.adapters.SupplierAdapter;
import io.github.zskamljic.restahead.intercepting.Chain;
import io.github.zskamljic.restahead.intercepting.Interceptor;
import io.github.zskamljic.restahead.spring.RestAheadService;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ConfigCombinations {
    private static final String BASE_URL = "https://httpbin.org/";

    public static class PassThroughInterceptor implements Interceptor {
        @Override
        public CompletableFuture<Response> intercept(Chain chain, Request request) {
            return chain.proceed(request);
        }
    }

    @RestAheadService(url = BASE_URL, client = DummyClient.class)
    interface ClientOnlyService {
        @Get("/get")
        Response get();
    }

    @RestAheadService(url = BASE_URL, client = DummyClient.class, interceptors = PassThroughInterceptor.class)
    interface ClientAndInterceptorService {
        @Get("/get")
        Response get();
    }

    @RestAheadService(url = BASE_URL, interceptors = PassThroughInterceptor.class)
    interface InterceptorService {
        @Get("/get")
        Response get();
    }

    @RestAheadService(url = BASE_URL, adapters = SupplierAdapter.class)
    interface AdapterService {
        @Get("/get")
        Supplier<Response> get();
    }
}
