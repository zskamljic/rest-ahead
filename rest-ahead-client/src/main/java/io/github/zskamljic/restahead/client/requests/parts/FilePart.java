package io.github.zskamljic.restahead.client.requests.parts;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class FilePart extends MultiPart {
    private final InputStream inputStream;
    private final String fileName;
    private final String contentType;

    public FilePart(String fieldName, String fileName, InputStream inputStream) {
        this(fieldName, fileName, null, inputStream);
    }

    public FilePart(String fieldName, String fileName, String contentType, InputStream inputStream) {
        super(fieldName);
        this.fileName = fileName;
        this.contentType = Optional.ofNullable(contentType).orElse("application/octet-stream");
        this.inputStream = inputStream;
    }

    public FilePart(String fieldName, Path path) throws IOException {
        this(fieldName, path.getFileName().toString(), path);
    }

    public FilePart(String fieldName, String fileName, Path path) throws IOException {
        this(fieldName, fileName, Files.probeContentType(path), Files.newInputStream(path));
    }

    public FilePart(String fieldName, File file) throws IOException {
        this(fieldName, file.getName(), file);
    }

    public FilePart(String fieldName, String fileName, File file) throws IOException {
        this(fieldName, fileName, Files.probeContentType(file.toPath()), new FileInputStream(file));
    }

    public FilePart(String fieldName, String fileName, byte[] body) {
        this(fieldName, fileName, new ByteArrayInputStream(body));
    }

    public FilePart(String fieldName, String fileName, String contentType, byte[] body) {
        this(fieldName, fileName, contentType, new ByteArrayInputStream(body));
    }

    @Override
    public String contentDisposition() {
        return super.contentDisposition() + "; filename=\"" + fileName + "\"" + SEPARATOR +
            "Content-Type: " + contentType;
    }

    @Override
    public InputStream bodyContent() {
        return inputStream;
    }
}
