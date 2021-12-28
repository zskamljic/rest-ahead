package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;

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