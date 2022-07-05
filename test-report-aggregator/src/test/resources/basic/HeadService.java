package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Head;
import io.github.zskamljic.restahead.client.responses.BodyResponse;
import io.github.zskamljic.restahead.client.responses.Response;

public interface HeadService {
    @Head
    void head();

    @Head
    BodyResponse<Void> head2();

    @Head
    Response head3();
}