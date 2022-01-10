package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.RestAhead;
import io.github.zskamljic.restahead.exceptions.RequestFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorizationServiceTest {
    private static final String TOKEN = "token";

    private AuthorizationService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder("https://httpbin.org/")
            .converter(new JacksonConverter())
            .build(AuthorizationService.class);
    }

    @Test
    void basicAuthHandles401() {
        var response = service.getBasicAuth("");

        assertEquals(401, response.status());
    }

    @Test
    void basicAuthSucceeds() {
        var response = service.getBasicAuth("Basic dXNlcjpwYXNzd29yZA==");

        assertEquals(200, response.status());
    }

    @Test
    void bearerAuthSucceeds() {
        var response = service.getBearer("Bearer " + TOKEN);

        assertTrue(response.authenticated());
        assertEquals(TOKEN, response.token());
    }

    @Test
    void bearerThrowsForNonSuccess() {
        assertThrows(RequestFailedException.class, () -> service.getBearer(""));
    }
}