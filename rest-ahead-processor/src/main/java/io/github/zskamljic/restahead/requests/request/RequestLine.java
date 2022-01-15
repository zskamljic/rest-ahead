package io.github.zskamljic.restahead.requests.request;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.requests.request.path.RequestPath;

/**
 * The processed request line.
 *
 * @param verb the verb to use for the request
 * @param path
 */
public record RequestLine(
    Verb verb,
    RequestPath path
) {
}
