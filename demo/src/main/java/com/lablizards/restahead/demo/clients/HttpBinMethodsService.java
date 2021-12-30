package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.request.Query;
import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;
import com.lablizards.restahead.demo.models.HttpBinResponse;

public interface HttpBinMethodsService {
    @Delete("/delete")
    HttpBinResponse delete(@Query("q") String query, @Header("Test-Header") String headers);

    @Get("/get")
    HttpBinResponse get(@Query("q") String query, @Header("Test-Header") String headers);

    @Patch("/patch")
    HttpBinResponse patch(@Query("q") String query, @Header("Test-Header") String headers);

    @Post("/post")
    HttpBinResponse post(@Query("q") String query, @Header("Test-Header") String headers);

    @Put("/put")
    HttpBinResponse put(@Query("q") String query, @Header("Test-Header") String headers);
}
