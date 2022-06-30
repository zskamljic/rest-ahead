package io.github.zskamljic.restahead.demo;

import java.util.Map;

import io.github.zskamljic.restahead.annotations.verbs.Head;

public interface HeadObjectService {
    @Head
    Map<String, Object> head();
}