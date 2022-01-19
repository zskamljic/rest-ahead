package io.github.zskamljic.restahead.demo.clients;

import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.RestAhead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormServiceTest {
    private static final String CONTENT_TYPE = "Content-Type";

    private FormService service;

    @BeforeEach
    void setUp() {
        service = RestAhead.builder("https://httpbin.org/")
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
}