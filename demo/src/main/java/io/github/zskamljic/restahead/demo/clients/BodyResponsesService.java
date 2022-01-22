package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.annotations.verbs.Put;
import io.github.zskamljic.restahead.client.responses.BodyAndErrorResponse;
import io.github.zskamljic.restahead.client.responses.BodyResponse;
import io.github.zskamljic.restahead.demo.models.HttpBinResponse;

import java.util.Map;

public interface BodyResponsesService {
    @Put("/put")
    BodyResponse<Map<String, Object>> put();

    @Get("/put")
    BodyAndErrorResponse<HttpBinResponse, Map<String, Object>> put2();
}
