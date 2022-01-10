package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.RestAhead;
import io.github.zskamljic.restahead.demo.adapters.SupplierAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomAdapterServiceTest {
    private CustomAdapterService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder("https://httpbin.org/")
            .converter(new JacksonConverter())
            .addAdapter(new SupplierAdapter())
            .build(CustomAdapterService.class);
    }

    @Test
    void supplierAdapterReturnsValue() {
        var response = service.get().get();

        assertNotNull(response);
    }
}