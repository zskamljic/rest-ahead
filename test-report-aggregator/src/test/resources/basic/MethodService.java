package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface MethodService {
    @Delete("/delete")
    default void delete() {
    }
}