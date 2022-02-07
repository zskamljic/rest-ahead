package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

import java.util.List;

public interface CollectionAndArray {
    @Delete("/delete")
    void deleteArray(@Query("q") String[] query);

    @Delete("/delete")
    void deleteList(@Query("q") List<String> query);

    @Delete("/delete")
    void deleteIntegerList(@Query("q") List<Integer> query);

    @Delete("/delete")
    void deletePrimitives(@Query("q") int query);
}