package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.demo.models.HttpBinResponse;

import java.util.function.Supplier;

public interface CustomAdapterService {
    @Get("/get")
    Supplier<HttpBinResponse> get();
}
