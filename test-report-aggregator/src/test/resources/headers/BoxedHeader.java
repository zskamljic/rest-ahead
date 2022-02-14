package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.annotations.verbs.Patch;
import io.github.zskamljic.restahead.annotations.verbs.Post;
import io.github.zskamljic.restahead.annotations.verbs.Put;

public interface BoxedHeader {
    @Delete("/delete")
    void delete(@Header("Accept") Boolean header);

    @Get("/delete")
    void delete(@Header("Accept") Byte header);

    @Patch("/delete")
    void delete(@Header("Accept") Character header);

    @Post("/delete")
    void delete(@Header("Accept") Double header);

    @Put("/delete")
    void delete(@Header("Accept") Float header);

    @Delete("/delete")
    void delete(@Header("Accept") Integer header);

    @Delete("/delete")
    void delete(@Header("Accept") Long header);

    @Delete("/delete")
    void delete(@Header("Accept") Short header);
}