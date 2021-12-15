package com.lablizards.restahead.generation;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Used to validate path parameter of HTTP verb annotations.
 */
public class PathValidator {
    private final Messager messager;

    /**
     * Create a new instance with messager that will receive all errors.
     *
     * @param messager the messager to report errors to
     */
    public PathValidator(Messager messager) {
        this.messager = messager;
    }

    /**
     * Check if any errors are present in the path, or if the function does not match required format.
     *
     * @param function the function for which path is validated
     * @param path     the path being validated
     * @return if there are any errors reported
     */
    public boolean containsErrors(ExecutableElement function, String path) {
        if (path.isEmpty()) return false;

        try {
            new URI(path);
        } catch (URISyntaxException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), function);
            return true;
        }
        return false;
    }

}
