package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface MissingQueryValue {
    @Delete("/delete?q=")
    void delete();
}