package io.github.zskamljic.restahead.client.requests.parts;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Common logic for both fields and files.
 */
public abstract sealed class MultiPart permits FieldPart, FilePart {
    protected static final String SEPARATOR = "\r\n";
    private final String fieldName;

    protected MultiPart(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Creates an input stream containing the boundary and content.
     *
     * @param boundary the boundary to use
     * @return the {@link InputStream} with written body
     */
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

    /**
     * Content disposition line for this type.
     *
     * @return the filled disposition
     */
    protected String contentDisposition() {
        return "Content-Disposition: form-data; name=\"" + fieldName + "\"";
    }

    /**
     * Get content for this part.
     *
     * @return the stream that when read will provide content of this part's body
     */
    protected abstract InputStream bodyContent();
}
