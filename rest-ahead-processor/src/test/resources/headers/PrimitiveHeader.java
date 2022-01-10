package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface PrimitiveHeader {
    @Delete("/delete")
    void delete(@Header("Accept") boolean header);

    @Delete("/delete")
    void delete(@Header("Accept") byte header);

    @Delete("/delete")
    void delete(@Header("Accept") char header);

    @Delete("/delete")
    void delete(@Header("Accept") double header);

    @Delete("/delete")
    void delete(@Header("Accept") float header);

    @Delete("/delete")
    void delete(@Header("Accept") int header);

    @Delete("/delete")
    void delete(@Header("Accept") long header);

    @Delete("/delete")
    void delete(@Header("Accept") short header);
}