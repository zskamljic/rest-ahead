package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface FormOnValidMap {
    @Post
    void post(@FormUrlEncoded @Body Map<String, String> body);

    @Post
    void post2(@FormUrlEncoded @Body Map<String, UUID> body);

    @Post
    void post3(@FormUrlEncoded @Body Map<String, Integer> body);

    @Post
    void post4(@FormUrlEncoded @Body HashMap<String, Double> body);
}