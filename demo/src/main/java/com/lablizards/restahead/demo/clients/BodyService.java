package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.annotations.request.Body;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;
import com.lablizards.restahead.demo.models.HttpBinResponse;

import java.util.Map;

public interface BodyService {
    @Get
    void get();

    @Patch("/patch")
    HttpBinResponse patch(@Body Map<String, Object> body);

    @Post("/post")
    HttpBinResponse post(@Body Map<String, Object> body);

    @Put("/put")
    HttpBinResponse put(@Body Map<String, Object> body);
}
