package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.annotations.form.FormName;
import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.form.Part;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.verbs.Post;
import io.github.zskamljic.restahead.client.requests.parts.FilePart;
import io.github.zskamljic.restahead.demo.models.ExternalFormBody;
import io.github.zskamljic.restahead.demo.models.HttpBinResponse;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public interface FormService {
    @Post("/post")
    HttpBinResponse post(@FormUrlEncoded @Body Map<String, String> body);

    @Post("/post")
    HttpBinResponse postRecord(@FormUrlEncoded @Body Sample body);

    @Post("/post")
    HttpBinResponse postClass(@FormUrlEncoded @Body SampleClass body);

    @Post("/post")
    HttpBinResponse postOtherModel(@FormUrlEncoded ExternalFormBody body);

    @Post("/post")
    HttpBinResponse postMultiPart(
        @Part String part,
        @Body @Part("two") String part2,
        @Part File file,
        @Part Path path,
        @Part FilePart input,
        @Part FilePart body
    );

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
