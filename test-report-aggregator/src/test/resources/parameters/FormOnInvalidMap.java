package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;

import java.util.Map;

public interface FormOnInvalidMap {
    @Post
    void post(@FormUrlEncoded @Body Map<String, Object> body);
}