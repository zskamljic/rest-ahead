package io.github.zskamljic.restahead.client.requests;

/**
 * The verb for HTTP request.
 */
public enum Verb {
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    PATCH,
    POST,
    PUT;

    /**
     * Whether the verb allows body.
     *
     * @return true if body can be present, false otherwise.
     */
    public boolean allowsBody() {
        return switch (this) {
            case DELETE, HEAD, GET, OPTIONS -> false;
            default -> true;
        };
    }
}
