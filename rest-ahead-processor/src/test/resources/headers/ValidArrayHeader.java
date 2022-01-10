package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface ValidArrayHeader {
    @Delete("/delete")
    void delete(@Header("Accept") String[] header);

    @Delete("/delete")
    void delete(@Header("Accept") int[] header);

    @Delete("/delete")
    void delete(@Header("Accept") Integer[] header);
}