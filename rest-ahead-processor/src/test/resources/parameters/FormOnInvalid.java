package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.verbs.Post;

public interface FormOnInvalid {
    @Post
    void post(@FormUrlEncoded @Header("head") String multiAnnotated);
}