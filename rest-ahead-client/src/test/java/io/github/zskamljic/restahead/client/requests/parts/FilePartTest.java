package io.github.zskamljic.restahead.client.requests.parts;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilePartTest {
    private static final String FIELD_NAME = "field";
    private static final String FILE_NAME = "fileName";

    @Test
    void inputFileGeneratesValidInput() throws IOException {
        var tmpFile = File.createTempFile("pre", "post");

        var input = new FilePart(FIELD_NAME, FILE_NAME, tmpFile);

        assertEquals("""
            --boundary\r
            Content-Disposition: form-data; name="field"; filename="fileName"\r
            Content-Type: application/octet-stream\r
            \r
            \r
            """, readStringFrom(input));
    }

    @Test
    void inputStreamGeneratesValidInput() throws IOException {
        var tmpFile = File.createTempFile("pre", "post");

        var input = new FilePart(FIELD_NAME, FILE_NAME, tmpFile.toPath());

        assertEquals("""
            --boundary\r
            Content-Disposition: form-data; name="field"; filename="fileName"\r
            Content-Type: application/octet-stream\r
            \r
            \r
            """, readStringFrom(input));
    }

    @Test
    void bytesGeneratesValidInput() throws IOException {
        var data = "data".getBytes();

        var input = new FilePart(FIELD_NAME, FILE_NAME, "application/octet-stream", data);

        assertEquals("""
            --boundary\r
            Content-Disposition: form-data; name="field"; filename="fileName"\r
            Content-Type: application/octet-stream\r
            \r
            data\r
            """, readStringFrom(input));
    }

    private String readStringFrom(FilePart field) throws IOException {
        return new String(field.inputStream("boundary").readAllBytes());
    }
}