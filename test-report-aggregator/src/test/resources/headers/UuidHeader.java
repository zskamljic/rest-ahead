package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

import java.util.UUID;

public interface UuidHeader {
    @Delete("/delete")
    void delete(@Header("Accept") UUID header);
}