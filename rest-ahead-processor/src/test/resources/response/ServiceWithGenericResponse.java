package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.Delete;

import java.util.Map;

public interface ServiceWithGenericResponse {
    @Delete("/delete")
    Map<String, Object> delete();
}