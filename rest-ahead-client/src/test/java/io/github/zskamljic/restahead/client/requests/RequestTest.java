package io.github.zskamljic.restahead.client.requests;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestTest {
    private static final String NAME = "Name";

    @Test
    void addHeaderAddsHeaders() {
        var request = new Request.Builder()
            .setBaseUrl("")
            .setVerb(Verb.GET)
            .addHeader(NAME, "one")
            .build();

        assertEquals(Map.of(NAME, List.of("one")), request.getHeaders());
    }

    @Test
    void addHeaderConsecutiveRetainsValues() {
        var request = new Request.Builder()
            .setBaseUrl("")
            .setVerb(Verb.GET)
            .addHeader(NAME, "one")
            .addHeader(NAME, "two")
            .build();

        assertEquals(Map.of(NAME, List.of("one", "two")), request.getHeaders());
    }

    @Test
    void addHeadersAddsAll() {
        var request = new Request.Builder()
            .setBaseUrl("")
            .setVerb(Verb.GET)
            .addHeaders(NAME, List.of("one", "two"))
            .build();

        assertEquals(Map.of(NAME, List.of("one", "two")), request.getHeaders());
    }

    @Test
    void addHeadersRetainsAll() {
        var request = new Request.Builder()
            .setBaseUrl("")
            .setVerb(Verb.GET)
            .addHeaders(NAME, List.of("one", "two"))
            .addHeader(NAME, "three")
            .build();

        assertEquals(Map.of(NAME, List.of("one", "two", "three")), request.getHeaders());
    }

    @Test
    void addHeaderAddsQueries() {
        var request = new Request.Builder()
            .setBaseUrl("")
            .setVerb(Verb.GET)
            .addQuery(NAME, "one")
            .build();

        assertEquals(Map.of(NAME, List.of("one")), request.getQueries());
    }

    @Test
    void addQueryConsecutiveRetainsValues() {
        var request = new Request.Builder()
            .setBaseUrl("")
            .setVerb(Verb.GET)
            .addQuery(NAME, "one")
            .addQuery(NAME, "two")
            .build();

        assertEquals(Map.of(NAME, List.of("one", "two")), request.getQueries());
    }

    @Test
    void addQueryAddsAll() {
        var request = new Request.Builder()
            .setBaseUrl("")
            .setVerb(Verb.GET)
            .addQueryItems(NAME, List.of("one", "two"))
            .build();

        assertEquals(Map.of(NAME, List.of("one", "two")), request.getQueries());
    }

    @Test
    void addQueryRetainsAll() {
        var request = new Request.Builder()
            .setBaseUrl("")
            .setVerb(Verb.GET)
            .addQueryItems(NAME, List.of("one", "two"))
            .addQuery(NAME, "three")
            .build();

        assertEquals(Map.of(NAME, List.of("one", "two", "three")), request.getQueries());
    }
}