package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.annotations.verbs.Post;

public interface Parameters {
    @Delete("/delete")
    void delete(String parameter);

    @Post
    void post(@Header("head") @Query("hello") String multiAnnotated);
}