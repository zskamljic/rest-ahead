package io.github.zskamljic.restahead.intercepting.logging;

import io.github.zskamljic.LocalServerExtension;
import io.github.zskamljic.LocalUrl;
import io.github.zskamljic.restahead.client.JavaHttpClient;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.requests.Verb;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(LocalServerExtension.class)
class LoggingInterceptorTest {
    @LocalUrl
    private String url;

    @Test
    void loggingLinesOutputsCorrect() throws ExecutionException, InterruptedException {
        var outputs = new ArrayList<String>();
        RequestLogger logger = outputs::add;

        var client = new JavaHttpClient();
        client.addInterceptor(new LoggingInterceptor.Builder()
            .logger(logger)
            .build());

        client.execute(new Request.Builder()
                .setVerb(Verb.GET)
                .setBaseUrl(url)
                .setPath("/get")
                .build())
            .get();

        assertEquals(2, outputs.size());
        assertEquals("""
            -> GET %s/get
            -> %s/get
            """.formatted(url, url), outputs.get(0));
        assertEquals("""
            <- 200 %s/get
            <- %s/get
            """.formatted(url, url), outputs.get(1));
    }

    @Test
    void disabledLoggerDoesNothing() throws ExecutionException, InterruptedException {
        var disabledLogger = spy(new RequestLogger() {
            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public void output(String output) {
                throw new RuntimeException("Should never be called");
            }
        });

        var client = new JavaHttpClient();
        client.addInterceptor(new LoggingInterceptor.Builder()
            .logger(disabledLogger)
            .build());

        client.execute(new Request.Builder()
                .setVerb(Verb.GET)
                .setBaseUrl(url)
                .setPath("/get")
                .build())
            .get();

        verify(disabledLogger).isEnabled();
        verifyNoMoreInteractions(disabledLogger);
    }

    @Test
    void loggingLinesOutputsCorrectWithHeaders() throws ExecutionException, InterruptedException {
        var outputs = new ArrayList<String>();
        RequestLogger logger = outputs::add;

        var client = new JavaHttpClient();
        client.addInterceptor(new LoggingInterceptor.Builder()
            .logger(logger)
            .logHeaders(true)
            .build());

        client.execute(new Request.Builder()
                .setVerb(Verb.GET)
                .addHeader("some", "header")
                .setBaseUrl(url)
                .setPath("/get")
                .build())
            .get();

        assertEquals(2, outputs.size());
        assertEquals("""
            -> GET %s/get
            some: header
            -> %s/get
            """.formatted(url, url), outputs.get(0));
        var response = outputs.get(1);
        assertTrue(response.startsWith("<- 200 %s/get\n".formatted(url)));
        assertTrue(response.contains("content-type: application/json"));
        assertTrue(response.contains("date:"));
        assertTrue(response.endsWith("<- %s/get\n".formatted(url)));
    }

    @Test
    void loggingLinesOutputsCorrectWithBody() throws ExecutionException, InterruptedException {
        var outputs = new ArrayList<String>();
        RequestLogger logger = outputs::add;

        var client = new JavaHttpClient();
        client.addInterceptor(new LoggingInterceptor.Builder()
            .logger(logger)
            .logBody(true)
            .build());

        client.execute(new Request.Builder()
                .setVerb(Verb.POST)
                .setBaseUrl(url)
                .setPath("/post")
                .setBody(new ByteArrayInputStream("form=value".getBytes()))
                .build())
            .get();

        assertEquals(2, outputs.size());
        assertEquals("""
            -> POST %s/post
                        
            form=value
            -> %s/post
            """.formatted(url, url), outputs.get(0));
        var response = outputs.get(1);
        assertTrue(response.startsWith("""
            <- 200 %s/post
                        
            """.formatted(url)));
        assertTrue(response.endsWith("""
            <- %s/post
            """.formatted(url)));
        assertTrue(response.contains("form=value"));
    }

    @Test
    void loggingLinesOutputsCorrectJoined() throws ExecutionException, InterruptedException {
        var outputs = new ArrayList<String>();
        RequestLogger logger = outputs::add;

        var client = new JavaHttpClient();
        client.addInterceptor(new LoggingInterceptor.Builder()
            .logger(logger)
            .joinRequestResponse(true)
            .build());

        client.execute(new Request.Builder()
                .setVerb(Verb.GET)
                .setBaseUrl(url)
                .setPath("/get")
                .build())
            .get();

        assertEquals(1, outputs.size());
        assertEquals("""
            -> GET %s/get
            -> %s/get
            <- 200 %s/get
            <- %s/get
            """.formatted(url, url, url, url), outputs.get(0));
    }
}