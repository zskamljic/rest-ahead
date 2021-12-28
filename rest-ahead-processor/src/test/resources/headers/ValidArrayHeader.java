package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Delete;

public interface ValidArrayHeader {
    @Delete("/delete")
    void delete(@Header("Accept") String[] header);

    @Delete("/delete")
    void delete(@Header("Accept") int[] header);

    @Delete("/delete")
    void delete(@Header("Accept") Integer[] header);
}