package com.lablizards.restahead.demo.clients;

import com.lablizards.restahead.JacksonConverter;
import com.lablizards.restahead.RestAhead;
import com.lablizards.restahead.demo.adapters.SupplierAdapter;
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