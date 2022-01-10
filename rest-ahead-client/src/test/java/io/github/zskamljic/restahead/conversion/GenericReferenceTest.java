package io.github.zskamljic.restahead.conversion;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericReferenceTest {
    @Test
    void genericReferenceReturnsParameterizedType() {
        var reference = new GenericReference<Map<String, Object>>() {
        };
        var type = reference.getType();

        assertTrue(type instanceof ParameterizedType);
        assertEquals("java.util.Map<java.lang.String, java.lang.Object>", type.toString());
    }
}