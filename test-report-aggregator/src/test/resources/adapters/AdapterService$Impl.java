package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.client.Client;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.exceptions.RestException;
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.String;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import javax.annotation.processing.Generated;

@Generated("Generated by RestAhead")
public final class AdapterService$Impl implements AdapterService {
    private final String baseUrl;

    private final Client client;

    private final AdapterService.StreamAdapter streamAdapter;

    public AdapterService$Impl(String baseUrl, Client client,
                               AdapterService.StreamAdapter streamAdapter) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.streamAdapter = streamAdapter;
    }

    @Override
    public final Stream<Response> delete() {
        var httpRequestBuilder = new Request.Builder()
            .setVerb(Verb.DELETE)
            .setBaseUrl(baseUrl)
            .setPath("/delete");
        var response = client.execute(httpRequestBuilder.build());
        try {
            return streamAdapter.adapt(response);
        } catch (ExecutionException | InterruptedException exception) {
            throw RestException.getAppropriateException(exception);
        }
    }
}