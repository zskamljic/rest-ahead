package io.github.zskamljic.restahead.intercepting.logging;

/**
 * Logger to use for output.
 */
public interface RequestLogger {
    /**
     * Whether this logger is enabled. Some loggers may only log for specific levels, this function can check if a level is enabled.
     *
     * @return if data should be logged or not
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Output the given string. Provided parameter may be multiline.
     *
     * @param output the data to output.
     */
    void output(String output);
}
