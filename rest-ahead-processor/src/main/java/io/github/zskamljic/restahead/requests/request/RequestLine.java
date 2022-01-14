package io.github.zskamljic.restahead.requests.request;

import io.github.zskamljic.restahead.client.requests.Verb;

/**
 * Contains the request verb and path.
 *
 * @param verb the verb for the request
 * @param path the path for the request
 */
public record RequestLine(
    Verb verb,
    String path
) {
    public boolean allowsBody() {
        return verb.allowsBody();
    }
}
