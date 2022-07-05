package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Head;
import io.github.zskamljic.restahead.annotations.verbs.Options;
import io.github.zskamljic.restahead.client.responses.BodyResponse;

public interface OptionsObjectService {
    @Options
    Map<String, Object> options();

    @Options
    BodyResponse<String> options2();
}