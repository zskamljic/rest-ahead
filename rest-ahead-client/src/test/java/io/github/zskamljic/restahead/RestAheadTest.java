package io.github.zskamljic.restahead;

import io.github.zskamljic.restahead.client.Client;
import io.github.zskamljic.restahead.conversion.Converter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class RestAheadTest {
    @Test
    void buildCreatesInstanceForClientOnly() {
        var instance = RestAhead.builder("https://httpbin.org")
            .build(SimpleGet.class);

        assertNotNull(instance);
    }

    @Test
    void buildCreatesInstanceForConverter() {
        var instance = RestAhead.builder("https://httpbin.org")
            .converter(mock(Converter.class))
            .build(ConverterGet.class);

        assertNotNull(instance);
    }

    @Test
    void buildThrowsForMissingConverter() {
        assertThrows(IllegalStateException.class, () -> {
            RestAhead.builder("https://httpbin.org")
                .build(ConverterGet.class);
        });
    }

    interface SimpleGet {
        void doGet();
    }

    interface ConverterGet extends SimpleGet {
    }
}

class SimpleGet$Impl implements RestAheadTest.SimpleGet {
    public SimpleGet$Impl(String url, Client client) {
    }

    @Override
    public void doGet() {
    }
}

class ConverterGet$Impl implements RestAheadTest.ConverterGet {
    public ConverterGet$Impl(String url, Client client, Converter converter) {
    }

    @Override
    public void doGet() {
    }
}
