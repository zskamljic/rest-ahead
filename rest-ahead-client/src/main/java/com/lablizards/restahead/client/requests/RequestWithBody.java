package com.lablizards.restahead.client.requests;

import java.io.InputStream;
import java.util.Optional;

/**
 * Requests that can potentially have a body.
 */
public sealed class RequestWithBody extends Request permits PatchRequest, PostRequest, PutRequest {
    private InputStream body;

    protected RequestWithBody(String path) {
        super(path);
    }

    /**
     * Set a body for this request
     *
     * @param body the body in form of {@link InputStream}
     */
    public void setBody(InputStream body) {
        this.body = body;
    }

    /**
     * Returns the body, if present.
     *
     * @return Body if present, empty otherwise
     */
    public Optional<InputStream> getBody() {
        return Optional.ofNullable(body);
    }
}
