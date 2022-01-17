package io.github.zskamljic.restahead.demo.clients;

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

    record Sample(String first, String second) {
    }
}
