package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface ValidCombinedQuery {
    @Delete("/delete?q=1")
    void delete(@Query("q") String query);
}