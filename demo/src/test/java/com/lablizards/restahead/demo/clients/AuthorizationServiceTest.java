package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.JacksonConverter;
import com.lablizards.restahead.RestAhead;
import com.lablizards.restahead.exceptions.RequestFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
    void basicAuthHandles401() throws ExecutionException, InterruptedException {
        var response = service.getBasicAuth("");

        assertEquals(401, response.get().status());
    }

    @Test
    void basicAuthSucceeds() throws ExecutionException, InterruptedException {
        var response = service.getBasicAuth("Basic dXNlcjpwYXNzd29yZA==");

        assertEquals(200, response.get().status());
    }

    @Test
    void bearerAuthSucceeds() throws ExecutionException, InterruptedException {
        var response = service.getBearer("Bearer " + TOKEN);

        assertTrue(response.get().authenticated());
        assertEquals(TOKEN, response.get().token());
    }

    @Test
    void bearerThrowsForNonSuccess() {
        try {
            service.getBearer("").get();
        } catch (ExecutionException e) {
            assertInstanceOf(RequestFailedException.class, e.getCause());
        } catch (InterruptedException e) {
            fail();
        }
        // assertThrows(ExecutionException.class, () -> service.getBearer("").get());
    }
}