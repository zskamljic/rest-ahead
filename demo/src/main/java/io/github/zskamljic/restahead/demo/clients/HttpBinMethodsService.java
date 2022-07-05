package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.annotations.verbs.Head;
import io.github.zskamljic.restahead.annotations.verbs.Options;
import io.github.zskamljic.restahead.annotations.verbs.Patch;
import io.github.zskamljic.restahead.annotations.verbs.Post;
import io.github.zskamljic.restahead.annotations.verbs.Put;
import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.demo.models.HttpBinResponse;

import java.util.List;

public interface HttpBinMethodsService {
    @Delete("/delete")
    HttpBinResponse delete(@Query("q") String query, @Header("Test-Header") String headers);

    @Get("/get")
    HttpBinResponse get(@Query("q") String query, @Header("Test-Header") String headers);

    @Head("/get")
    void head();

    @Options("/get")
    List<Verb> options();

    @Patch("/patch")
    HttpBinResponse patch(@Query("q") String query, @Header("Test-Header") String headers);

    @Post("/post")
    HttpBinResponse post(@Query("q") String query, @Header("Test-Header") String headers);

    @Put("/put")
    HttpBinResponse put(@Query("q") String query, @Header("Test-Header") String headers);
}
