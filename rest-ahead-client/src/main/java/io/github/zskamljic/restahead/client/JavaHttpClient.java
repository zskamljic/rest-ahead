package io.github.zskamljic.restahead.client;

import io.github.zskamljic.restahead.client.requests.DeleteRequest;
import io.github.zskamljic.restahead.client.requests.GetRequest;
import io.github.zskamljic.restahead.client.requests.PatchRequest;
import io.github.zskamljic.restahead.client.requests.PostRequest;
import io.github.zskamljic.restahead.client.requests.PutRequest;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.requests.RequestWithBody;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementation of {@link RestClient} using {@link HttpClient} present in JDK.
 */
public class JavaHttpClient implements RestClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String baseUrl;

    /**
     * Creates a new instance of this client.
     *
     * @param baseUrl the url to use as the base of the request
     */
    public JavaHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public CompletableFuture<Response> execute(Request request) {
        var queryString = request.getQueries()
            .entrySet()
            .stream()
            .flatMap(entry -> entry.getValue().stream().map(value -> "%s=%s".formatted(entry.getKey(), value)))
            .collect(Collectors.joining("&"));

        var requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + request.getPath() + "?" + queryString));
        if (request instanceof DeleteRequest) {
            requestBuilder.DELETE();
        } else if (request instanceof GetRequest) {
            requestBuilder.GET();
        } else if (request instanceof PatchRequest patchRequest) {
            requestBuilder.method("PATCH", selectBodyPublisher(patchRequest));
        } else if (request instanceof PostRequest postRequest) {
            requestBuilder.POST(selectBodyPublisher(postRequest));
        } else if (request instanceof PutRequest putRequest) {
            requestBuilder.PUT(selectBodyPublisher(putRequest));
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
    private HttpRequest.BodyPublisher selectBodyPublisher(RequestWithBody request) {
        return request.getBody()
            .map(input -> HttpRequest.BodyPublishers.ofInputStream(() -> input))
            .orElse(HttpRequest.BodyPublishers.noBody());
    }
}
