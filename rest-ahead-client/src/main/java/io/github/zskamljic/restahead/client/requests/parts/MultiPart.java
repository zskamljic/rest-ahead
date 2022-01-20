package io.github.zskamljic.restahead.client.requests.parts;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public abstract sealed class MultiPart permits FieldPart, FilePart {
    protected static final String SEPARATOR = "\r\n";
    private final String fieldName;

    protected MultiPart(String fieldName) {
        this.fieldName = fieldName;
    }

    public final InputStream inputStream(String boundary) {
        var prefix = "--" + boundary + SEPARATOR +
            contentDisposition() + SEPARATOR + SEPARATOR;

        var prefixStream = new ByteArrayInputStream(prefix.getBytes(StandardCharsets.US_ASCII));
        var suffixStream = new ByteArrayInputStream(SEPARATOR.getBytes(StandardCharsets.US_ASCII));
        return new SequenceInputStream(Collections.enumeration(List.of(
            prefixStream,
            bodyContent(),
            suffixStream
        )));
    }

    protected String contentDisposition() {
        return "Content-Disposition: form-data; name=\"" + fieldName + "\"";
    }

    protected abstract InputStream bodyContent();
}
