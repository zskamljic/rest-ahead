package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.HttpBinRunner;
import io.github.zskamljic.restahead.HttpBinUrl;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(HttpBinRunner.class)
class FormServiceTest {
    @HttpBinUrl
    private static String url;

    private static final String CONTENT_TYPE = "Content-Type";

    private FormService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder(url)
            .converter(new JacksonConverter())
            .build(FormService.class);
    }

    @Test
    void formRequestSendsCorrectData() {
        var requestFormData = Map.of("key", "value", "key1", "value1");
        var response = service.post(requestFormData);

        var headers = response.headers();
        assertEquals("application/x-www-form-urlencoded", headers.get(CONTENT_TYPE).get(0));
        assertEquals(Map.of("key", List.of("value"), "key1", List.of("value1")), response.form());
    }

    @Test
    void formRequestSendsCorrectDataForRecord() {
        var sample = new FormService.Sample("FIRST", "SECOND");

        var response = service.postRecord(sample);

        var headers = response.headers();
        assertEquals("application/x-www-form-urlencoded", headers.get(CONTENT_TYPE).get(0));
        assertEquals(Map.of("first", List.of("FIRST"), "2nd", List.of("SECOND")), response.form());
    }

    @Test
    void formRequestSendsCorrectDataForClass() {
        var sample = new FormService.SampleClass("FIRST", "SECOND");

        var response = service.postClass(sample);

        var headers = response.headers();
        assertEquals("application/x-www-form-urlencoded", headers.get(CONTENT_TYPE).get(0));
        assertEquals(Map.of("first", List.of("FIRST"), "2nd", List.of("SECOND")), response.form());
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
        assertTrue(headers.get(CONTENT_TYPE).get(0).startsWith("multipart/form-data;"));
        assertEquals(4, response.files().size());
        var files = assertInstanceOf(List.class, response.files().get("file"));
        assertFalse(files.isEmpty());
        assertEquals(List.of(""), response.files().get("path"));
        assertEquals(List.of("data"), response.files().get("stream"));
        assertEquals(2, response.form().size());
        assertEquals(List.of("part1"), response.form().get("part"));
        assertEquals(List.of("part2"), response.form().get("two"));
    }

    @Test
    void formRequestSendsNamedField() {
        var response = service.postOtherModel(new ExternalFormBody("hello"));

        assertEquals(Map.of("snake_case", List.of("hello")), response.form());
    }
}