package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface InterfaceWithNotAnnotatedMethod {
    @Delete("/delete")
    void delete();

    void missingAnnotation();
}