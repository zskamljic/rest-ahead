package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Delete;

import java.util.Collection;
import java.util.List;

public interface ValidCollectionHeader {
    @Delete("/delete")
    void delete(@Header("Accept") List<String> header);

    @Delete("/delete")
    void delete(@Header("Accept") Collection<String> header);

    @Delete("/delete")
    void deleteInts(@Header("Accept") Collection<Integer> header);
}