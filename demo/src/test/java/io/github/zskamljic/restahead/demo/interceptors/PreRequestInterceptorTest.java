package io.github.zskamljic.restahead.demo.interceptors;

import io.github.zskamljic.LocalServerExtension;
import io.github.zskamljic.LocalUrl;
import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.RestAhead;
import io.github.zskamljic.restahead.client.JavaHttpClient;
import io.github.zskamljic.restahead.demo.clients.InterceptedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(LocalServerExtension.class)
class PreRequestInterceptorTest {
    @LocalUrl
    private String localUrl;

    @Test
    void interceptorIsCalled() {
        var client = new JavaHttpClient();
        client.addInterceptor(new PreRequestInterceptor());

        var service = RestAhead.builder(localUrl)
            .client(client)
            .converter(new JacksonConverter())
            .build(InterceptedService.class);

        var response = service.get();
        assertNotNull(response);
        assertEquals(200, response.status());
    }
}