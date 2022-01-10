package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface InvalidPath {
    @Delete("/delete with invalid path")
    void delete();
}