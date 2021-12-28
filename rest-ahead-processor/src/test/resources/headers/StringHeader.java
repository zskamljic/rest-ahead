package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Delete;

public interface StringHeader {
    @Delete("/delete")
    void delete(@Header("Accept") String header);
}