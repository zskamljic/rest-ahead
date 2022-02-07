package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface MissingName {
    @Delete("/delete")
    void delete(@Query("") String query);
}