package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.form.Part;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;

public interface FormWithPartInvalidType {
    @Post
    void post(@Body @Part Object body);
}