package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.JacksonConverter;
import com.lablizards.restahead.RestAhead;
import com.lablizards.restahead.demo.models.HttpBinResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpBinMethodsServiceTest {
    private static final String QUERY_NAME = "q";
    private static final String QUERY = "query";
    private static final String HEADER_NAME = "Test-Header";
    private static final String HEADER = "Header";

    private HttpBinMethodsService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder("https://httpbin.org/")
            .converter(new JacksonConverter())
            .build(HttpBinMethodsService.class);
    }

    @Test
    void deleteRequestSucceeds() {
        var response = service.delete(QUERY, HEADER);
        performAssertions(response, true);
    }

    @Test
    void getRequestSucceeds() {
        var response = service.get(QUERY, HEADER);
        performAssertions(response, false);
    }

    @Test
    void patchRequestSucceeds() {
        var response = service.patch(QUERY, HEADER);
        performAssertions(response, true);
    }

    @Test
    void postRequestSucceeds() {
        var response = service.post(QUERY, HEADER);
        performAssertions(response, true);
    }

    @Test
    void putRequestSucceeds() {
        var response = service.put(QUERY, HEADER);
        performAssertions(response, true);
    }

    void performAssertions(HttpBinResponse response, boolean hasBody) {
        assertNotNull(response.headers());
        assertTrue(response.headers().containsKey(HEADER_NAME));
        assertEquals(HEADER, response.headers().get(HEADER_NAME));

        assertNotNull(response.args());
        assertTrue(response.args().containsKey(QUERY_NAME));
        assertEquals(QUERY, response.args().get(QUERY_NAME));

        if (hasBody) {
            assertNotNull(response.data());
            assertTrue(response.data().isEmpty());

            assertNotNull(response.files());
            assertTrue(response.files().isEmpty());
        } else {
            assertNull(response.data());

            assertNull(response.files());
        }
    }
}