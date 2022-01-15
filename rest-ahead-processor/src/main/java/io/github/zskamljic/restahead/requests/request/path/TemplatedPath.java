package io.github.zskamljic.restahead.requests.request.path;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a templated path, that will need to be filled before use.
 */
public final class TemplatedPath extends RequestPath {
    private final String cleanedPath;
    private final List<String> requiredParameters = new ArrayList<>();

    public TemplatedPath(String path) {
        super(path);
        cleanedPath = path.replaceAll(PATH_VARIABLE.pattern(), "$1$2");
        var matcher = PATH_VARIABLE.matcher(path);
        while (matcher.find()) {
            requiredParameters.add(matcher.group(2));
        }
    }

    /**
     * Find the parameters that need to be provided.
     *
     * @return list of parameters
     */
    public List<String> getRequiredParameters() {
        return requiredParameters;
    }

    /**
     * Creates a valid, parsed version of the internal path with the query. Parameters will be unwrapped for the representation.
     *
     * @return the parsed URI
     * @throws URISyntaxException if URI is malformed
     */
    @Override
    public URI uri() throws URISyntaxException {
        return new URI(cleanedPath);
    }
}
