package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.JacksonConverter;
import com.lablizards.restahead.RestAhead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FutureServiceTest {
    private FutureService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder("https://httpbin.org/")
            .converter(new JacksonConverter())
            .build(FutureService.class);
    }

    @Test
    void futureGetSucceeds() throws ExecutionException, InterruptedException {
        var response = service.getFuture().get();

        assertNotNull(response);
    }

    @Test
    void completableFutureGetSucceeds() throws ExecutionException, InterruptedException {
        var response = service.getCompletableFuture().get();

        assertNotNull(response);
    }
}