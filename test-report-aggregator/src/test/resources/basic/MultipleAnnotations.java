package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.annotations.verbs.Patch;

public interface MultipleAnnotations {
    @Delete("/delete")
    @Patch("/delete")
    void delete();
}