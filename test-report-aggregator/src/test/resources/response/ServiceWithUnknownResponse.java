package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface ServiceWithUnknownResponse {
    @Delete("/delete")
    TestResponse delete();

    record TestResponse() {
    }
}