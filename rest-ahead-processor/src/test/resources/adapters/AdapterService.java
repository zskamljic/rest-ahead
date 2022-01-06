package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.Adapter;
import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.client.Response;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public interface AdapterService {
    @Delete("/delete")
    Stream<Response> delete();

    static class StreamAdapter {
        @Adapter
        public <T> Stream<T> adapt(Future<T> response) throws ExecutionException, InterruptedException {
            return Stream.of(response.get());
        }
    }
}