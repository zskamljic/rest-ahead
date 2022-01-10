package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.annotations.verbs.Patch;
import io.github.zskamljic.restahead.annotations.verbs.Post;
import io.github.zskamljic.restahead.annotations.verbs.Put;
import io.github.zskamljic.restahead.demo.models.HttpBinResponse;

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
