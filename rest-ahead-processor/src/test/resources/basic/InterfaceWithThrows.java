package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.client.responses.Response;

import java.io.IOException;

public interface InterfaceWithThrows {
    @Delete("/delete")
    Response delete() throws IOException;
}