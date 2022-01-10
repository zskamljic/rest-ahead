package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.demo.models.HttpBinResponse;

import java.util.function.Supplier;

public interface CustomAdapterService {
    @Get("/get")
    Supplier<HttpBinResponse> get();
}
