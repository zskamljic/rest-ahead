package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface ValidQuery {
    @Delete("/delete")
    void delete(@Query("q") String query);
}