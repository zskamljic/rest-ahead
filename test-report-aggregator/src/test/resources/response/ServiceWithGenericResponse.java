package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;

import java.util.Map;

public interface ServiceWithGenericResponse {
    @Delete("/delete")
    Map<String, Object> delete();
}