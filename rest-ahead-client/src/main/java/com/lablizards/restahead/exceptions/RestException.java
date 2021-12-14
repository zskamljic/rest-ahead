package com.lablizards.restahead.exceptions;

public class RestException extends RuntimeException {
    public RestException(Throwable throwable) {
        super(throwable);
    }
}
