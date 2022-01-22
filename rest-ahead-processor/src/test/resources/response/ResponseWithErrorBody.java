package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.client.responses.BodyAndErrorResponse;

import java.util.Map;

public interface ResponseWithErrorBody {
    @Delete("/delete")
    BodyAndErrorResponse<SomeObject, SomeObject> delete();

    @Delete("/delete")
    BodyAndErrorResponse<Map<String, Object>, Map<String, Object>> deleteMap();

    record SomeObject() {
    }
}