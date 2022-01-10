package io.github.zskamljic.restahead.exceptions;

import java.io.InputStream;

public class RequestFailedException extends RestException {
    private final int code;
    private final transient InputStream errorBody;

    public RequestFailedException(int code, InputStream inputStream) {
        super("Request failed with code " + code);
        this.code = code;
        this.errorBody = inputStream;
    }

    public int getCode() {
        return code;
    }

    public InputStream getErrorBody() {
        return errorBody;
    }
}
