package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Head;
import io.github.zskamljic.restahead.client.responses.BodyResponse;

public interface HeadService {
    @Head
    void head();

    @Head
    BodyResponse<Void> head2();
}