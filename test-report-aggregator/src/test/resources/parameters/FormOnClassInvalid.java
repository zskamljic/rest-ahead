package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;

public interface FormOnClassInvalid {
    @Post
    void post(@FormUrlEncoded @Body Sample body);

    class Sample {
        public String getFirst() {
            return "FIRST";
        }

        public Object getSecond() {
            return 4;
        }
    }
}