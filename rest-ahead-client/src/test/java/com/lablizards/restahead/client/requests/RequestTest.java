package com.lablizards.restahead.client.requests;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestTest {
    private static final String NAME = "Name";

    @Test
    void addHeaderAddsHeaders() {
        var request = new GetRequest("");

        request.addHeader(NAME, "one");

        assertEquals(Map.of(NAME, List.of("one")), request.getHeaders());
    }

    @Test
    void addHeaderConsecutiveRetainsValues() {
        var request = new GetRequest("");

        request.addHeader(NAME, "one");
        request.addHeader(NAME, "two");

        assertEquals(Map.of(NAME, List.of("one", "two")), request.getHeaders());
    }

    @Test
    void addHeadersAddsAll() {
        var request = new GetRequest("");

        request.addHeaders(NAME, List.of("one", "two"));

        assertEquals(Map.of(NAME, List.of("one", "two")), request.getHeaders());
    }

    @Test
    void addHeadersRetainsAll() {
        var request = new GetRequest("");

        request.addHeaders(NAME, List.of("one", "two"));
        request.addHeader(NAME, "three");

        assertEquals(Map.of(NAME, List.of("one", "two", "three")), request.getHeaders());
    }

    @Test
    void addHeaderAddsQueries() {
        var request = new GetRequest("");

        request.addQuery(NAME, "one");

        assertEquals(Map.of(NAME, List.of("one")), request.getQueries());
    }

    @Test
    void addQueryConsecutiveRetainsValues() {
        var request = new GetRequest("");

        request.addQuery(NAME, "one");
        request.addQuery(NAME, "two");

        assertEquals(Map.of(NAME, List.of("one", "two")), request.getQueries());
    }

    @Test
    void addQueryAddsAll() {
        var request = new GetRequest("");

        request.addQueryItems(NAME, List.of("one", "two"));

        assertEquals(Map.of(NAME, List.of("one", "two")), request.getQueries());
    }

    @Test
    void addQueryRetainsAll() {
        var request = new GetRequest("");

        request.addQueryItems(NAME, List.of("one", "two"));
        request.addQuery(NAME, "three");

        assertEquals(Map.of(NAME, List.of("one", "two", "three")), request.getQueries());
    }
}