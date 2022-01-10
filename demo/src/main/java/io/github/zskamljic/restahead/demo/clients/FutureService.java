package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.demo.models.HttpBinResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface FutureService {
    @Get("/get")
    Future<HttpBinResponse> getFuture();

    @Get("/get")
    CompletableFuture<HttpBinResponse> getCompletableFuture();
}
