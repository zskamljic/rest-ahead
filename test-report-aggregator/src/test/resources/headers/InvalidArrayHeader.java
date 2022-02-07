package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface InvalidArrayHeader {
    @Delete("/delete")
    void delete(@Header("Accept") Object[] header);
}