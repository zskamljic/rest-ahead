package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.client.Response;

public interface ServiceWithResponse {
    @Delete("/delete")
    Response delete();
}