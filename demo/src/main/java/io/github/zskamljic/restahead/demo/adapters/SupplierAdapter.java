package io.github.zskamljic.restahead.demo.adapters;

import io.github.zskamljic.restahead.annotations.Adapter;
import io.github.zskamljic.restahead.exceptions.RestException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Demonstration on how an adapter can be declared.
 */
public class SupplierAdapter {
    @Adapter
    public <T> Supplier<T> adapt(Future<T> future) {
        return () -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RestException(e);
            }
        };
    }
}