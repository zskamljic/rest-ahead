package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.annotations.Adapter;
import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.client.responses.Response;

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