package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.client.responses.Response;

public interface InterceptedService {
    @Get("/invalid")
    Response get();
}
