package io.github.zskamljic.restahead.polyglot;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class ProcessingException extends Exception {
    private final transient Element target;
    private final String message;

    public ProcessingException(Element target, String message) {
        this.target = target;
        this.message = message;
    }

    public void report(Messager messager) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, target);
    }
}
