package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.verbs.Delete;

import java.util.UUID;

public interface UuidHeader {
    @Delete("/delete")
    void delete(@Header("Accept") UUID header);
}