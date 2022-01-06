package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Body;
import com.lablizards.restahead.annotations.verbs.Post;

import java.util.Map;

public interface BodyService {
    @Post("/post")
    void post(@Body Map<String, Object> body);
}