package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.LocalServerExtension;
import io.github.zskamljic.LocalUrl;
import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.RestAhead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(LocalServerExtension.class)
class FutureServiceTest {
    @LocalUrl
    private String localUrl;
    private FutureService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder(localUrl)
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