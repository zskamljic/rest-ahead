package io.github.zskamljic.restahead.client;

import io.github.zskamljic.restahead.client.requests.Request;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of {@link Client} using {@link HttpClient} present in JDK.
 */
public class JavaHttpClient extends Client {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public CompletableFuture<Response> performRequest(Request request) {
        var requestBuilder = HttpRequest.newBuilder()
            .uri(request.uri());
        switch (request.getVerb()) {
            case DELETE -> requestBuilder.DELETE();
            case GET -> requestBuilder.GET();
            case PATCH -> requestBuilder.method("PATCH", selectBodyPublisher(request));
            case POST -> requestBuilder.POST(selectBodyPublisher(request));
            case PUT -> requestBuilder.PUT(selectBodyPublisher(request));
        }
        request.getHeaders().forEach((name, values) -> values.forEach(value -> requestBuilder.header(name, value)));
        var httpRequest = requestBuilder.build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
            .thenApply(response -> new Response(response.statusCode(), response.headers().map(), response.body()));
    }

    /**
     * Creates a body publisher for the given request.
     *
     * @param request the request with body, from which to get the body
     * @return the appropriate body publisher
     */
    private HttpRequest.BodyPublisher selectBodyPublisher(Request request) {
        return request.getBody()
            .map(input -> HttpRequest.BodyPublishers.ofInputStream(() -> input))
            .orElse(HttpRequest.BodyPublishers.noBody());
    }
}
