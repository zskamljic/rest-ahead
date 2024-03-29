package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.form.FormName;
import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;

public interface FormOnRecord {
    @Post
    void post(@FormUrlEncoded @Body Sample body);

    @Post
    void post2(@FormUrlEncoded Sample body);

    record Sample(String first, @FormName("2nd") String second) {
    }
}