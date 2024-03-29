package io.github.zskamljic.restahead.adapter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The default, built-in adapters for RestAhead.
 */
public class DefaultAdapters {
    /**
     * Adapter to convert async Future calls to blocking calls.
     *
     * @param response the response to convert
     * @param <T>      the type that can be converted automatically
     * @return the value of provided future
     * @throws ExecutionException   if an error occurred during future execution
     * @throws InterruptedException if the future was interrupted
     */
    public <T> T syncAdapter(Future<T> response) throws ExecutionException, InterruptedException {
        return response.get();
    }

    /**
     * Converts Future calls to blocking call, without a response.
     *
     * @param response the response to convert
     * @param <T>      the of the request
     * @throws ExecutionException   if an error occurred during future execution
     * @throws InterruptedException if the future was interrupted
     */
    public <T> void syncVoidAdapter(Future<T> response) throws ExecutionException, InterruptedException {
        response.get();
    }
}
