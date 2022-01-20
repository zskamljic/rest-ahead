package io.github.zskamljic.restahead.client.requests.parts;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldPartTest {
    private static final String FIELD_NAME = "name";

    @Test
    void stringValueGeneratesCorrectStream() throws IOException {
        var field = new FieldPart(FIELD_NAME, "value");

        var data = readStringFrom(field);

        assertEquals("""
            --boundary\r
            Content-Disposition: form-data; name="name"\r
            \r
            value\r
            """, data);
    }

    @Test
    void uuidValueGeneratesCorrectStream() throws IOException {
        var uuid = UUID.randomUUID();
        var field = new FieldPart(FIELD_NAME, uuid);

        var data = readStringFrom(field);

        assertEquals("""
            --boundary\r
            Content-Disposition: form-data; name="name"\r
            \r
            %s\r
            """.formatted(uuid), data);
    }

    @Test
    void numberGeneratesCorrectStream() throws IOException {
        var field = new FieldPart(FIELD_NAME, 5);

        var data = readStringFrom(field);

        assertEquals("""
            --boundary\r
            Content-Disposition: form-data; name="name"\r
            \r
            %s\r
            """.formatted(5), data);
    }

    @Test
    void charactersCorrectStream() throws IOException {
        var field = new FieldPart(FIELD_NAME, 'a');

        var data = readStringFrom(field);

        assertEquals("""
            --boundary\r
            Content-Disposition: form-data; name="name"\r
            \r
            %s\r
            """.formatted('a'), data);
    }

    private String readStringFrom(FieldPart field) throws IOException {
        return new String(field.inputStream("boundary").readAllBytes());
    }
}