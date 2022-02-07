package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

import java.util.Collection;
import java.util.List;

public interface InvalidCollectionHeader {
    @Delete("/delete")
    void delete(@Header("Accept") List<Object> header);

    @Delete("/delete")
    void delete(@Header("Accept") Collection<Object> header);
}