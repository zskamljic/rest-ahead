package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;
import com.lablizards.restahead.client.Response;

import java.io.IOException;

public interface HttpBinMethodsService {
    @Delete("/delete")
    Response delete() throws IOException;

    @Get("/get")
    Response get() throws IOException, InterruptedException;

    @Patch("/patch")
    Response patch(@Header("Accept") String... accept);

    @Post("/post")
    Response post();

    @Put("/put")
    Response put();
}
