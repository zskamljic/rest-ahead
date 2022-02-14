package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;

public class NormalClassMethod {
    @Delete("/delete")
    void delete() {
    }
}