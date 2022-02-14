package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.form.FormName;
import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;

public interface FormOnWithWrongField {
    @Post
    void post(@FormUrlEncoded @Body Sample body);

    record Sample(String first, @FormName("") String second) {
    }
}