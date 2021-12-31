package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.request.Query;
import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;
import com.lablizards.restahead.demo.models.HttpBinResponse;

import java.util.concurrent.Future;

public interface HttpBinMethodsService {
    @Delete("/delete")
    Future<HttpBinResponse> delete(@Query("q") String query, @Header("Test-Header") String headers);

    @Get("/get")
    Future<HttpBinResponse> get(@Query("q") String query, @Header("Test-Header") String headers);

    @Patch("/patch")
    Future<HttpBinResponse> patch(@Query("q") String query, @Header("Test-Header") String headers);

    @Post("/post")
    Future<HttpBinResponse> post(@Query("q") String query, @Header("Test-Header") String headers);

    @Put("/put")
    Future<HttpBinResponse> put(@Query("q") String query, @Header("Test-Header") String headers);
}
