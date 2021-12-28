package com.lablizards.restahead.client.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common superclass for all requests, contains functionality and data common to all requests.
 */
public abstract sealed class Request permits DeleteRequest, GetRequest, PatchRequest, PostRequest, PutRequest {
    private final Map<String, List<String>> headers = new HashMap<>();
    private final Map<String, List<String>> query = new HashMap<>();
    protected String path;

    protected Request(String path) {
        this.path = path;
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
     * Adds a header for the given name.
     *
     * @param name  the header name
     * @param value the header value
     */
    public void addHeader(String name, String value) {
        headers.putIfAbsent(name, new ArrayList<>());
        headers.get(name).add(value);
    }

    /**
     * Adds multiple headers to the request.
     *
     * @param name   the name of the header
     * @param values the values for the given header
     */
    public void addHeaders(String name, List<String> values) {
        headers.merge(name, new ArrayList<>(values), (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        });
    }

    /**
     * Get all the headers for this request.
     *
     * @return the headers with their values
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Adds a query parameter for the given name.
     *
     * @param name  the query parameter name
     * @param value the query parameter value
     */
    public void addQuery(String name, String value) {
        headers.putIfAbsent(name, new ArrayList<>());
        headers.get(name).add(value);
    }

    /**
     * Adds multiple query elements to the request.
     *
     * @param name   the name of the query
     * @param values the values for the given query
     */
    public void addQueryItems(String name, List<String> values) {
        headers.merge(name, new ArrayList<>(values), (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        });
    }

    /**
     * Get all the queries for this request.
     *
     * @return the queries with their values
     */
    public Map<String, List<String>> getQueries() {
        return query;
    }
}
