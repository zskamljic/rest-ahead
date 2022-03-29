package io.github.zskamljic.restahead.intercepting.logging;

public class BodyLoggingException extends RuntimeException {
    public BodyLoggingException(Exception exception) {
        super(exception);
    }
}
