package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.JacksonConverter;
import com.lablizards.restahead.RestAhead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class BodyServiceTest {
    private BodyService service;

    private static final Map<String, Object> DEMO_BODY = Map.of(
        "simple", 123,
        "object", Map.of(
            "subobject", "string"
        )
    );

    @BeforeEach
    void setUp() {
        service = RestAhead.builder("https://httpbin.org/")
            .converter(new JacksonConverter())
            .build(BodyService.class);
    }

    @Test
    void getCompletesNormally() {
        try {
            service.get();
        } catch (Exception e) {
            fail("Method must not throw");
        }
    }

    @Test
    void patchRequestsSendsBody() {
        var response = service.patch(DEMO_BODY);

        assertEquals(DEMO_BODY, response.json());
    }

    @Test
    void postRequestsSendsBody() {
        var response = service.post(DEMO_BODY);

        assertEquals(DEMO_BODY, response.json());
    }

    @Test
    void putRequestsSendsBody() {
        var response = service.put(DEMO_BODY);

        assertEquals(DEMO_BODY, response.json());
    }
}