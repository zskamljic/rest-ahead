package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;

public interface FormOnClass {
    @Post
    void post(@FormUrlEncoded @Body Sample body);

    class Sample {
        public String getFirst() {
            return "FIRST";
        }

        public int getSecond() {
            return 4;
        }

        public static void getSomething() {

        }

        private Object getIgnored() {
            return null;
        }
    }
}