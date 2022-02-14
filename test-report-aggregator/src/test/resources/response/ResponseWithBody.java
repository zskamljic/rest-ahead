package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.client.responses.BodyResponse;

import java.util.Map;

public interface ResponseWithBody {
    @Delete("/delete")
    BodyResponse<SomeObject> delete();

    @Delete("/delete")
    BodyResponse<Map<String, Object>> deleteMap();

    record SomeObject() {
    }
}