package io.github.zskamljic.restahead.exceptions;

import java.util.concurrent.ExecutionException;

/**
 * Exception used for general errors during execution of RestAhead generated classes.
 */
public class RestException extends RuntimeException {
    public RestException(Throwable throwable) {
        super(throwable);
    }

    public RestException(String message) {
        super(message);
    }

    /**
     * Unwraps the exception or constructs a new one depending on type to prevent nesting causes.
     *
     * @param throwable the throwable to unwrap or wrap
     * @return the RestException for the given throwable
     */
    public static RestException getAppropriateException(Throwable throwable) {
        if (throwable instanceof ExecutionException exec) {
            return getAppropriateException(exec.getCause());
        } else if (throwable instanceof RestException restException) {
            return restException;
        }
        return new RestException(throwable);
    }
}
