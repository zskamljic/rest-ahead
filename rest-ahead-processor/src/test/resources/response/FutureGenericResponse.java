package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.Delete;

import java.util.Map;
import java.util.concurrent.Future;

public interface FutureGenericResponse {
    @Delete("/delete")
    Future<Map<String, Object>> delete();
}