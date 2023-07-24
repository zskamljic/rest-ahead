package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.LocalServerExtension;
import io.github.zskamljic.LocalUrl;
import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.RestAhead;
import io.github.zskamljic.restahead.demo.adapters.SupplierAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(LocalServerExtension.class)
class CustomAdapterServiceTest {
    @LocalUrl
    private String url;
    private CustomAdapterService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder(url)
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