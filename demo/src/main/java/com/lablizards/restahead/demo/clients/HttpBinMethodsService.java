package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.request.Query;
import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;
import com.lablizards.restahead.client.Response;

import java.io.IOException;
import java.util.Map;

public interface HttpBinMethodsService {
    @Delete("/delete")
    Map<String, Object> delete(@Query("q") String... search) throws IOException;

    @Get("/get")
    Object get() throws IOException, InterruptedException;

    @Patch("/patch")
    Response patch(@Header("Accept") String... accept);

    @Post("/post")
    Response post();

    @Put("/put")
    Response put();
}
