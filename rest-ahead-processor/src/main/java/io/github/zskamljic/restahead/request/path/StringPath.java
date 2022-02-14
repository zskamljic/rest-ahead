package io.github.zskamljic.restahead.request.path;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Wraps the path string, where no substitutions need to be done.
 */
public final class StringPath extends RequestPath {
    public StringPath(String path) {
        super(path);
    }

    /**
     * Parses the given path URI.
     *
     * @return the parsed URI
     * @throws URISyntaxException if any invalid characters are present or the format is not valid.
     */
    @Override
    public URI uri() throws URISyntaxException {
        return new URI(path);
    }
}
