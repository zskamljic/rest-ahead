package com.lablizards.restahead.client.requests;

/**
 * Common superclass for all requests, contains functionality and data common to all requests.
 */
public abstract sealed class Request permits DeleteRequest, GetRequest, PatchRequest, PostRequest, PutRequest {
    protected String path;

    protected Request(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
