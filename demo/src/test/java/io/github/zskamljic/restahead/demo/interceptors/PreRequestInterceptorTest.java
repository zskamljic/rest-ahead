package io.github.zskamljic.restahead.demo.interceptors;

import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.RestAhead;
import io.github.zskamljic.restahead.client.JavaHttpClient;
import io.github.zskamljic.restahead.demo.clients.InterceptedService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PreRequestInterceptorTest {
    @Test
    void interceptorIsCalled() {
        var client = new JavaHttpClient();
        client.addInterceptor(new PreRequestInterceptor());

        var service = RestAhead.builder("https://httpbin.org/")
            .client(client)
            .converter(new JacksonConverter())
            .build(InterceptedService.class);

        var response = service.get();
        assertNotNull(response);
        assertEquals(200, response.status());
    }
}