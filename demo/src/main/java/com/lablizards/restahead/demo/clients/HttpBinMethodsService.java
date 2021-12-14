package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;
import com.lablizards.restahead.client.Response;

public interface HttpBinMethodsService {
    @Delete("/delete")
    Response delete();

    @Get("/get")
    Response get();

    @Patch("/patch")
    Response patch();

    @Post("/post")
    Response post();

    @Put("/put")
    Response put();
}
