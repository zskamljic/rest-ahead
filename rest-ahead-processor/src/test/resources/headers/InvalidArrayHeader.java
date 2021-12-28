package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Delete;

public interface InvalidArrayHeader {
    @Delete("/delete")
    void delete(@Header("Accept") Object[] header);
}