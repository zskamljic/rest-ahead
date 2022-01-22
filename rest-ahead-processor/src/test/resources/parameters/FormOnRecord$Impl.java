package io.github.zskamljic.restahead.demo;

import io.github.zskamljic.restahead.adapter.DefaultAdapters;
import io.github.zskamljic.restahead.client.Client;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.exceptions.RestException;
import io.github.zskamljic.restahead.generation.FormConverter;
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.String;
import java.util.concurrent.ExecutionException;
import javax.annotation.processing.Generated;

@Generated("Generated by RestAhead")
public final class FormOnRecord$Impl implements FormOnRecord {
    private final String baseUrl;

    private final Client client;

    private final DefaultAdapters defaultAdapters;

    public FormOnRecord$Impl(String baseUrl, Client client, DefaultAdapters defaultAdapters) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.defaultAdapters = defaultAdapters;
    }

    @Override
    public final void post(FormOnRecord.Sample body) {
        var httpRequestBuilder = new Request.Builder()
            .setVerb(Verb.POST)
            .setBaseUrl(baseUrl)
            .setPath("");
        httpRequestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpRequestBuilder.setBody(FormConverter.formEncode(body));
        var response = client.execute(httpRequestBuilder.build());
        try {
            defaultAdapters.syncVoidAdapter(response);
        } catch (ExecutionException | InterruptedException exception) {
            throw RestException.getAppropriateException(exception);
        }
    }
}