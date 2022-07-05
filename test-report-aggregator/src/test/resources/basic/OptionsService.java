package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Options;
import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.client.responses.Response;

import java.util.List;

public interface OptionsService {
    @Options
    void options();

    @Options
    List<Verb> options2();

    @Options
    Response options3();
}