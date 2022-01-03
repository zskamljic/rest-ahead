package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.demo.models.HttpBinResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface FutureService {
    @Get("/get")
    Future<HttpBinResponse> getFuture();

    @Get("/get")
    CompletableFuture<HttpBinResponse> getCompletableFuture();
}
