package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface QueryInPath {
    @Delete("/delete?q=1")
    void delete();
}