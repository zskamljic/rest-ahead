package io.github.zskamljic.restahead.polyglot;

import java.util.List;

public class CompositeProcessingException extends Exception {
    private final List<ProcessingException> exceptions;

    public CompositeProcessingException(List<ProcessingException> exceptions) {
        this.exceptions = exceptions;
    }

    public List<ProcessingException> getExceptions() {
        return exceptions;
    }
}
