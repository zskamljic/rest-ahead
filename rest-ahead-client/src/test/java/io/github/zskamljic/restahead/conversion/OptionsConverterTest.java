package io.github.zskamljic.restahead.conversion;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.client.responses.Response;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionsConverterTest {
    @Test
    void generateOptionsListWildcard() {
        var headers = Map.of(
            "Allow", List.of("*")
        );
        var response = new Response(0, headers, InputStream.nullInputStream());

        var verbs = OptionsConverter.parseOptions(response);
        assertTrue(Arrays.stream(Verb.values()).allMatch(verbs::contains));
    }

    @Test
    void generateOptionsListDuplicates() {
        var headers = Map.of(
            "Allow", List.of("GET", "GET")
        );
        var response = new Response(0, headers, InputStream.nullInputStream());

        var verbs = OptionsConverter.parseOptions(response);
        assertEquals(1, verbs.size());
        assertEquals(Verb.GET, verbs.get(0));
    }

    @Test
    void generateOptionsListMultiple() {
        var headers = Map.of(
            "Allow", List.of("GET", "POST")
        );
        var response = new Response(0, headers, InputStream.nullInputStream());

        var verbs = OptionsConverter.parseOptions(response);
        assertEquals(2, verbs.size());
        assertTrue(verbs.contains(Verb.POST));
        assertTrue(verbs.contains(Verb.GET));
    }
}