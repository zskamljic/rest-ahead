package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.RestAhead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BodyResponsesServiceTest {
    private BodyResponsesService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder("https://httpbin.org/")
            .converter(new JacksonConverter())
            .build(BodyResponsesService.class);
    }

    @Test
    void putReturnsBodyAndCode() {
        var response = service.put();

        assertEquals(200, response.status());
        assertTrue(response.body().isPresent());
        assertTrue(response.errorBody().isEmpty());
    }
}