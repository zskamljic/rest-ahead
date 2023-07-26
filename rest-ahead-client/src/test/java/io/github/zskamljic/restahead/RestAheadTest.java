package io.github.zskamljic.restahead;

import io.github.zskamljic.LocalServerExtension;
import io.github.zskamljic.LocalUrl;
import io.github.zskamljic.restahead.client.Client;
import io.github.zskamljic.restahead.conversion.Converter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(LocalServerExtension.class)
class RestAheadTest {
    @LocalUrl
    private String localUrl;

    @Test
    void buildCreatesInstanceForClientOnly() {
        var instance = RestAhead.builder(localUrl)
            .build(SimpleGet.class);

        assertNotNull(instance);
    }

    @Test
    void buildCreatesInstanceForConverter() {
        var instance = RestAhead.builder(localUrl)
            .converter(mock(Converter.class))
            .build(ConverterGet.class);

        assertNotNull(instance);
    }

    @Test
    void buildThrowsForMissingConverter() {
        assertThrows(IllegalStateException.class, () -> RestAhead.builder(localUrl)
            .build(ConverterGet.class));
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
