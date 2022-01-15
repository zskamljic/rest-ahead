package io.github.zskamljic.restahead.requests.request.path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplatedPathTest {
    @ParameterizedTest
    @CsvSource({
        "path,{path}",
        "/path,/{path}",
        "/path/path,/path/{path}",
        "/path/path1/path2/path3,/path/{path1}/{path2}/path3",
        "/path,/{path}?q=1"
    })
    void cleanedPathReturnsValidPath(String expected, String input) throws URISyntaxException {
        var templatedPath = new TemplatedPath(input);

        assertEquals(expected, templatedPath.uri().getPath());
    }

    @Test
    void requiredParametersReturnsCorrectList() {
        var string = "/path/{path1}/{path2}/path3";
        var templatedPath = new TemplatedPath(string);

        assertEquals(List.of("path1", "path2"), templatedPath.getRequiredParameters());
    }

    @Test
    void toStringReturnsPathWithoutQuery() {
        var string = "/{path}?q=1";
        var templatedPath = new TemplatedPath(string);

        assertEquals("/{path}", templatedPath.toString());
    }
}