package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;

import java.util.Map;

public interface BodyService {
    @Post("/post")
    void post(@Body Map<String, Object> body);
}