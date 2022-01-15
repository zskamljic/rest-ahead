package io.github.zskamljic.restahead.requests.request.path;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * The common request path logic.
 */
public abstract sealed class RequestPath permits StringPath, TemplatedPath {
    protected static final Pattern PATH_VARIABLE = Pattern.compile("(^|/)\\{(\\w+)}(?=$|/|\\?)");
    protected final String path;

    protected RequestPath(String path) {
        this.path = path;
    }

    /**
     * Creates a valid, parsed version of the internal path with the query.
     *
     * @return the parsed URI
     * @throws URISyntaxException if URI is malformed
     */
    public abstract URI uri() throws URISyntaxException;

    /**
     * Returns only the path from provided parameter.
     *
     * @return clean, unmodified path only substring
     */
    @Override
    public String toString() {
        var queryStart = path.indexOf("?");
        if (queryStart < 0) {
            return path;
        }
        return path.substring(0, queryStart);
    }

    /**
     * Parse the path with query, returning appropriate.
     *
     * @param path the path to parse
     * @return {@link TemplatedPath} if there's any placeholders present, {@link StringPath} if no processing is required
     */
    public static RequestPath parse(String path) {
        if (PATH_VARIABLE.matcher(path).find()) {
            return new TemplatedPath(path);
        }
        return new StringPath(path);
    }
}
