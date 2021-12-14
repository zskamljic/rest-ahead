package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;

public interface HttpBinMethodsService {
    @Delete("/delete")
    void delete();

    @Get("/get")
    void get();

    @Patch("/patch")
    void patch();

    @Post("/post")
    void post();

    @Put("/put")
    void put();
}
