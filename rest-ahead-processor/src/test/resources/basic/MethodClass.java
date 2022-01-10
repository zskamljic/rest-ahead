package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;

public abstract class MethodClass {
    @Delete("/delete")
    abstract void delete();
}