package io.github.zskamljic.restahead;

import io.github.zskamljic.restahead.client.RestClient;
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

    static class SimpleGet$Impl implements SimpleGet {
        public SimpleGet$Impl(RestClient client) {
        }

        @Override
        public void doGet() {
        }
    }

    interface ConverterGet extends SimpleGet {
    }

    static class ConverterGet$Impl implements ConverterGet {
        public ConverterGet$Impl(RestClient client, Converter converter) {
        }

        @Override
        public void doGet() {
        }
    }
}
