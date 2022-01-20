package io.github.zskamljic.restahead.client.requests;

import io.github.zskamljic.restahead.client.requests.parts.MultiPart;

import java.io.ByteArrayInputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MultiPartRequest {
    private final List<MultiPart> parts = new ArrayList<>();

    private MultiPartRequest() {
    }

    public static MultiPartRequest builder() {
        return new MultiPartRequest();
    }

    public MultiPartRequest addPart(MultiPart part) {
        parts.add(part);
        return this;
    }

    String generateBoundary() {
        return "----" + UUID.randomUUID();
    }

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
