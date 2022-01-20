package io.github.zskamljic.restahead.client.requests;

import io.github.zskamljic.restahead.client.requests.parts.MultiPart;

import java.io.ByteArrayInputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This class can be used to generate multipart request bodies.
 */
public class MultiPartRequest {
    private final List<MultiPart> parts = new ArrayList<>();

    private MultiPartRequest() {
    }

    /**
     * Starts building a new instance.
     *
     * @return reference to the builder
     */
    public static MultiPartRequest builder() {
        return new MultiPartRequest();
    }

    /**
     * Adds a new part of the request.
     *
     * @param part the part to add
     * @return reference to the builder
     */
    public MultiPartRequest addPart(MultiPart part) {
        parts.add(part);
        return this;
    }

    /**
     * Generates a boundary string.
     *
     * @return the boundary string to use
     */
    String generateBoundary() {
        return "----" + UUID.randomUUID();
    }

    /**
     * Builds the specified parts into a target request, setting header and body.
     *
     * @param requestBuilder the builder for which to set the header and body
     */
    public void buildInto(Request.Builder requestBuilder) {
        var boundary = generateBoundary();
        requestBuilder.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        var streams = parts.stream()
            .map(part -> part.inputStream(boundary))
            .toList();

        var body = new SequenceInputStream(
            new SequenceInputStream(Collections.enumeration(streams)),
            new ByteArrayInputStream(("--" + boundary + "--").getBytes(StandardCharsets.US_ASCII))
        );
        requestBuilder.setBody(body);
    }
}
