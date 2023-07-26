package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.LocalServerExtension;
import io.github.zskamljic.LocalUrl;
import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.RestAhead;
import io.github.zskamljic.restahead.client.requests.parts.FilePart;
import io.github.zskamljic.restahead.demo.models.ExternalFormBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(LocalServerExtension.class)
class FormServiceTest {
    @LocalUrl
    private String localUrl;
    private static final String CONTENT_TYPE = "Content-Type";

    private FormService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder(localUrl)
            .converter(new JacksonConverter())
            .build(FormService.class);
    }

    @Test
    void formRequestSendsCorrectData() {
        var formData = Map.of("key", "value", "key1", "value1");
        var response = service.post(formData);

        var headers = response.headers();
        assertEquals("application/x-www-form-urlencoded", headers.get(CONTENT_TYPE));
        assertEquals(formData, response.form());
    }

    @Test
    void formRequestSendsCorrectDataForRecord() {
        var sample = new FormService.Sample("FIRST", "SECOND");

        var response = service.postRecord(sample);

        var headers = response.headers();
        assertEquals("application/x-www-form-urlencoded", headers.get(CONTENT_TYPE));
        assertEquals(Map.of("first", "FIRST", "2nd", "SECOND"), response.form());
    }

    @Test
    void formRequestSendsCorrectDataForClass() {
        var sample = new FormService.SampleClass("FIRST", "SECOND");

        var response = service.postClass(sample);

        var headers = response.headers();
        assertEquals("application/x-www-form-urlencoded", headers.get(CONTENT_TYPE));
        assertEquals(Map.of("first", "FIRST", "2nd", "SECOND"), response.form());
    }

    @Test
    void formRequestSendsMultipart() throws IOException {
        var path = Files.createTempFile("pre", "post");
        var file = path.toFile();
        var input = new ByteArrayInputStream("data".getBytes());

        var inputPart = new FilePart("stream", "stream", input);
        var bytesPart = new FilePart("bytes", "bytes", new byte[]{1, 2, 3});

        var response = service.postMultiPart("part1", "part2", file, path, inputPart, bytesPart);

        var headers = response.headers();
        assertTrue(headers.get(CONTENT_TYPE).startsWith("multipart/form-data;"));
        assertEquals(4, response.files().size());
        assertEquals("", response.files().get("file"));
        assertEquals("", response.files().get("path"));
        assertEquals("data", response.files().get("stream"));
        assertEquals(2, response.form().size());
        assertEquals("part1", response.form().get("part"));
        assertEquals("part2", response.form().get("two"));
    }

    @Test
    void formRequestSendsNamedField() {
        var response = service.postOtherModel(new ExternalFormBody("hello"));

        assertEquals(Map.of("snake_case", "hello"), response.form());
    }
}