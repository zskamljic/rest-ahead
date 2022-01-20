package io.github.zskamljic.restahead.client.requests.parts;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Represents a multipart field.
 */
public final class FieldPart extends MultiPart {
    private final String value;

    public FieldPart(String fieldName, String value) {
        super(fieldName);
        this.value = value;
    }

    public FieldPart(String fieldName, UUID value) {
        this(fieldName, value.toString());
    }

    public FieldPart(String fieldName, Number number) {
        this(fieldName, String.valueOf(number));
    }

    public FieldPart(String fieldName, Character character) {
        this(fieldName, String.valueOf(character));
    }

    @Override
    protected InputStream bodyContent() {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}
