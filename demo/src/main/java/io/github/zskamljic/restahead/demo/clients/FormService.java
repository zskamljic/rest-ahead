package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.annotations.form.FormName;
import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;
import io.github.zskamljic.restahead.demo.models.HttpBinResponse;

import java.util.Map;

public interface FormService {
    @Post("/post")
    HttpBinResponse post(@FormUrlEncoded @Body Map<String, String> body);

    @Post("/post")
    HttpBinResponse postRecord(@FormUrlEncoded @Body Sample body);

    @Post("/post")
    HttpBinResponse postClass(@FormUrlEncoded @Body SampleClass body);

    record Sample(String first, @FormName("2nd") String second) {
    }

    class SampleClass {
        private final String first;
        private final String second;

        public SampleClass(String first, String second) {
            this.first = first;
            this.second = second;
        }

        public String getFirst() {
            return first;
        }

        @FormName("2nd")
        public String getSecond() {
            return second;
        }
    }
}
