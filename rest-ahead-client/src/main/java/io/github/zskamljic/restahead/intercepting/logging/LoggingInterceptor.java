package io.github.zskamljic.restahead.intercepting.logging;

import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.intercepting.Chain;
import io.github.zskamljic.restahead.intercepting.Interceptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Use to log request and response data, URL and headers based on configuration.
 */
public class LoggingInterceptor implements Interceptor {
    private final RequestLogger logger;
    private final boolean joinRequestResponse;
    private final boolean logHeaders;
    private final boolean logBody;

    /**
     * Create an instance using provided logger
     *
     * @param logger              the logger to output the data to
     * @param joinRequestResponse whether request and response will be output with a single log
     * @param logHeaders          whether to log headers
     * @param logBody             whether to log the body
     */
    private LoggingInterceptor(RequestLogger logger, boolean joinRequestResponse, boolean logHeaders, boolean logBody) {
        this.logger = logger;
        this.joinRequestResponse = joinRequestResponse;
        this.logHeaders = logHeaders;
        this.logBody = logBody;
    }

    @Override
    public CompletableFuture<Response> intercept(Chain chain, Request request) {
        if (!logger.isEnabled()) {
            return chain.proceed(request);
        }

        var logBuilder = new StringBuilder();
        var uri = request.uri();
        try {
            request = logRequest(logBuilder, uri, request);
        } catch (BodyLoggingException e) {
            return CompletableFuture.failedFuture(e.getCause());
        }

        return chain.proceed(request)
            .thenApply(response -> logResponse(logBuilder, uri, response));
    }

    /**
     * Logs the request.
     *
     * @param logBuilder the builder to log data to
     * @param uri        the uri of the request
     * @param request    the request
     * @return the request to use to proceed with the chain
     */
    private Request logRequest(StringBuilder logBuilder, URI uri, Request request) {
        logBuilder.append("-> ").append(request.getVerb()).append(" ").append(uri).append("\n");
        logHeaders(logBuilder, request.getHeaders());
        if (logBody) {
            var stream = request.getBody()
                .map(body -> logBody(logBuilder, body));
            var originalRequest = request;
            request = stream.map(value -> originalRequest.buildUpon().setBody(value).build())
                .orElse(originalRequest);
        }
        logBuilder.append("-> ").append(uri).append("\n");

        if (!joinRequestResponse) {
            logger.output(logBuilder.toString());
            logBuilder.setLength(0);
        }
        return request;
    }

    /**
     * Log the response data.
     *
     * @param logBuilder the builder where info will be added
     * @param uri        the uri that was used
     * @param response   the response of the request
     * @return the response to propagate through the chain
     */
    private Response logResponse(StringBuilder logBuilder, URI uri, Response response) {
        logBuilder.append("<- ").append(response.status()).append(" ").append(uri).append("\n");
        logHeaders(logBuilder, response.headers());

        if (logBody) {
            try {
                logBuilder.append("\n");
                var stream = logBody(logBuilder, response.body());
                response = new Response(response.status(), response.headers(), stream);
            } catch (BodyLoggingException e) {
                throw new CompletionException(e.getCause());
            }
        }
        logBuilder.append("<- ").append(uri).append("\n");
        logger.output(logBuilder.toString());
        return response;
    }

    /**
     * Log the headers present in the request.
     *
     * @param logBuilder the string builder to output the data to
     * @param headers    the headers to log
     */
    private void logHeaders(StringBuilder logBuilder, Map<String, List<String>> headers) {
        if (!logHeaders) return;

        headers.forEach((name, values) ->
            values.forEach(value ->
                logBuilder.append(name)
                    .append(": ")
                    .append(value)
                    .append("\n")
            )
        );
    }

    /**
     * Adds the body content to the specified builder. Constructs a new body stream to ensure existing data was not consumed.
     *
     * @param logBuilder  the builder where body should be added
     * @param inputStream the body source
     * @return new stream to use for body
     * @throws BodyLoggingException if original body could not be read
     */
    private InputStream logBody(StringBuilder logBuilder, InputStream inputStream) {
        try {
            var bodyBytes = inputStream.readAllBytes();
            logBuilder.append("\n").append(new String(bodyBytes, StandardCharsets.UTF_8)).append("\n");
            return new ByteArrayInputStream(bodyBytes);
        } catch (IOException e) {
            throw new BodyLoggingException(e);
        }
    }

    /**
     * Used to build a new instance of LoggingInterceptor.
     */
    public static class Builder {
        private RequestLogger logger;
        private boolean joinRequestResponse;
        private boolean logHeaders;
        private boolean logBody;

        public Builder() {
        }

        /**
         * Set the logger to use. If not set, System.out is used.
         *
         * @param logger the logger to use
         * @return this builder
         */
        public Builder logger(RequestLogger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Configure joining request and response body when logging. When true request will only be printed when response data is obtained and handled.
         * If false, request and response will be printed separately. If true data will be output in a single batch. Note:
         * if any errors happen during response handling (request fails) the request data will not be printed.
         *
         * @param join true for joining, false for split logging
         * @return this builder
         */
        public Builder joinRequestResponse(boolean join) {
            joinRequestResponse = join;
            return this;
        }

        /**
         * Enable or disable logging of headers. Disabled by default.
         *
         * @param logHeaders true to log headers, false to skip them
         * @return this builder
         */
        public Builder logHeaders(boolean logHeaders) {
            this.logHeaders = logHeaders;
            return this;
        }

        /**
         * Enable or disable logging of bodies. Disabled by default.
         *
         * @param logBody true to log body, false to skip them
         * @return this builder
         */
        public Builder logBody(boolean logBody) {
            this.logBody = logBody;
            return this;
        }

        /**
         * Build the instance of LoggingInterceptor
         *
         * @return the final interceptor
         */
        public LoggingInterceptor build() {
            if (logger == null) {
                logger = System.out::println;
            }
            return new LoggingInterceptor(logger, joinRequestResponse, logHeaders, logBody);
        }
    }
}
