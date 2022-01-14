package io.github.zskamljic.restahead.client.requests;

import io.github.zskamljic.restahead.util.StringMultiMap;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Common superclass for all requests, contains functionality and data common to all requests.
 */
public final class Request {
    private final Verb verb;
    private final String baseUrl;
    private final String path;
    private final StringMultiMap headers;
    private final StringMultiMap query;
    private final InputStream body;

    private Request(
        Verb verb,
        String baseUrl,
        String path,
        StringMultiMap headers,
        StringMultiMap query,
        InputStream body
    ) {
        this.verb = verb;
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        this.baseUrl = baseUrl;
        this.path = path;
        this.headers = headers;
        this.query = query;
        this.body = body;
    }

    /**
     * Returns the {@link Verb} associated with the request.
     *
     * @return the verb
     */
    public Verb getVerb() {
        return verb;
    }

    /**
     * Returns the base url used with this request.
     *
     * @return the baseUrl
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Get the path of the request.
     *
     * @return the request path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get all the headers for this request. Values returned are immutable.
     *
     * @return the headers with their values
     */
    public Map<String, List<String>> getHeaders() {
        return headers.immutableCopy();
    }

    /**
     * Get all the queries for this request. Values returned are immutable.
     *
     * @return the queries with their values
     */
    public Map<String, List<String>> getQueries() {
        return query.immutableCopy();
    }

    /**
     * Gets the body of the request, if present.
     *
     * @return body if present, empty if body is not present
     */
    public Optional<InputStream> getBody() {
        return Optional.ofNullable(body);
    }

    /**
     * Constructs the URI with values present in this request.
     *
     * @return the combined URI with baseUrl, path and query
     */
    public URI uri() {
        var queryString = query.entrySet()
            .stream()
            .flatMap(entry -> entry.getValue().stream().map(value -> "%s=%s".formatted(entry.getKey(), value)))
            .collect(Collectors.joining("&"));

        String pathWithoutSlash;
        if (path.startsWith("/")) {
            pathWithoutSlash = path.substring(1);
        } else {
            pathWithoutSlash = path;
        }
        return URI.create(baseUrl + pathWithoutSlash + "?" + queryString);
    }

    /**
     * Creates a new builder populated with values from this request.
     *
     * @return a new builder
     */
    public Builder buildUpon() {
        return new Builder(this);
    }

    /**
     * Builder used to construct instances of {@link Request}.
     */
    public static class Builder {
        private Verb verb;
        private String baseUrl;
        private String path;
        private final StringMultiMap headers;
        private final StringMultiMap query;
        private InputStream body;

        /**
         * Begin constructing an empty request.
         */
        public Builder() {
            headers = new StringMultiMap();
            query = new StringMultiMap();
        }

        /**
         * Begin constructing a new request from original request.
         *
         * @param request the request on top of which to build
         */
        private Builder(Request request) {
            verb = request.verb;
            baseUrl = request.baseUrl;
            path = request.path;
            headers = request.headers.mutableCopy();
            query = request.query.mutableCopy();
            body = request.body;
        }

        /**
         * Sets the verb for the request.
         *
         * @param verb the verb to use
         * @return instance of the builder
         */
        public Builder setVerb(Verb verb) {
            this.verb = verb;
            return this;
        }

        /**
         * Sets the baseUrl for the request.
         *
         * @param baseUrl the verb to use
         * @return instance of the builder
         */
        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets the path for the request.
         *
         * @param path the verb to use
         * @return instance of the builder
         */
        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        /**
         * Removes all currently present headers.
         *
         * @return instance of the builder
         */
        public Builder clearHeaders() {
            headers.clear();
            return this;
        }

        /**
         * Removes all currently present query items.
         *
         * @return instance of the builder
         */
        public Builder clearQuery() {
            query.clear();
            return this;
        }

        /**
         * Sets the body used for this request.
         *
         * @param body the body to use
         * @return instance of the builder
         */
        public Builder setBody(InputStream body) {
            this.body = body;
            return this;
        }

        /**
         * Adds a header for the given name.
         *
         * @param name  the header name
         * @param value the header value
         */
        public Builder addHeader(String name, String value) {
            headers.putIfAbsent(name, new ArrayList<>());
            headers.get(name).add(value);
            return this;
        }

        /**
         * Adds multiple headers to the request.
         *
         * @param name   the name of the header
         * @param values the values for the given header
         */
        public Builder addHeaders(String name, List<String> values) {
            headers.merge(name, new ArrayList<>(values), (l1, l2) -> {
                l1.addAll(l2);
                return l1;
            });
            return this;
        }

        /**
         * Adds a query parameter for the given name.
         *
         * @param name  the query parameter name
         * @param value the query parameter value
         */
        public Builder addQuery(String name, String value) {
            query.putIfAbsent(name, new ArrayList<>());
            query.get(name).add(value);
            return this;
        }

        /**
         * Adds multiple query elements to the request.
         *
         * @param name   the name of the query
         * @param values the values for the given query
         */
        public Builder addQueryItems(String name, List<String> values) {
            query.merge(name, new ArrayList<>(values), (l1, l2) -> {
                l1.addAll(l2);
                return l1;
            });
            return this;
        }

        /**
         * Constructs an instance of the request with data from the builder.
         *
         * @return the request
         * @throws NullPointerException     if verb or baseUrl are missing
         * @throws IllegalArgumentException if body has been set, but verb does not support it
         */
        public Request build() {
            Objects.requireNonNull(verb, "Verb must be set");
            Objects.requireNonNull(baseUrl, "Base URL must be set");
            var actualPath = Optional.ofNullable(path).orElse("");
            if (!verb.allowsBody() && body != null) {
                throw new IllegalStateException(verb + " does not allow a body.");
            }

            return new Request(verb, baseUrl, actualPath, headers, query, body);
        }
    }
}
