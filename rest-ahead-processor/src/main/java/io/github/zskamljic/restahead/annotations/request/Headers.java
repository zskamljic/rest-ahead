package io.github.zskamljic.restahead.annotations.request;

/**
 * Add static headers to the request.
 */
public @interface Headers {
    String[] value();
}
