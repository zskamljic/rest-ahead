package com.lablizards.restahead;

import com.lablizards.restahead.client.Response;
import com.lablizards.restahead.conversion.GenericReference;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonConverterTest {
    @Test
    void deserializeWorksForNormalType() throws IOException {
        var converter = new JacksonConverter();

        var result = converter.deserialize(new Response(200, Map.of(), new ByteArrayInputStream("""
            {
                "a": true
            }
            """.getBytes())), Object.class);

        assertNotNull(result);
    }

    @Test
    void deserializeWorksForGenericTypes() throws IOException {
        var converter = new JacksonConverter();

        Map<String, Boolean> result = converter.deserialize(new Response(200, Map.of(), new ByteArrayInputStream("""
            {
                "a": true
            }
            """.getBytes())), new GenericReference<Map<String, Boolean>>() {
        }.getType());

        assertNotNull(result);
        assertInstanceOf(Map.class, result);
        assertTrue(result.containsKey("a"));
        assertTrue(result.get("a"));
    }
}