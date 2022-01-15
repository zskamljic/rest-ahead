package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Path;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface PathAnnotationCodeName {
    @Delete("/{path}")
    void delete(@Path String path);
}