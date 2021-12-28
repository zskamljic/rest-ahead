package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Delete;

import java.util.List;

public interface InvalidCollectionHeader {
    @Delete("/delete")
    void delete(@Header("Accept") List<Object> header);

    @Delete("/delete")
    void delete(@Header("Accept") Collection<Object> header);
}