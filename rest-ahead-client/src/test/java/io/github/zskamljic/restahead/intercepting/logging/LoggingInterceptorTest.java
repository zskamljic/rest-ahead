package io.github.zskamljic.restahead.intercepting.logging;

import io.github.zskamljic.restahead.client.JavaHttpClient;
import io.github.zskamljic.restahead.client.requests.Request;
import io.github.zskamljic.restahead.client.requests.Verb;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class LoggingInterceptorTest {
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
                .setBaseUrl("https://httpbin.org")
                .setPath("/get")
                .build())
            .get();

        assertEquals(2, outputs.size());
        assertEquals("""
            -> GET https://httpbin.org/get
            -> https://httpbin.org/get
            """, outputs.get(0));
        assertEquals("""
            <- 200 https://httpbin.org/get
            <- https://httpbin.org/get
            """, outputs.get(1));
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
                .setBaseUrl("https://httpbin.org")
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
                .setBaseUrl("https://httpbin.org")
                .setPath("/get")
                .build())
            .get();

        assertEquals(2, outputs.size());
        assertEquals("""
            -> GET https://httpbin.org/get
            some: header
            -> https://httpbin.org/get
            """, outputs.get(0));
        var response = outputs.get(1);
        assertTrue(response.startsWith("""
            <- 200 https://httpbin.org/get
            :status: 200
            access-control-allow-credentials: true
            access-control-allow-origin: *
            content-length: 264
            content-type: application/json
            """));
        assertTrue(response.endsWith("""
            <- https://httpbin.org/get
            """));
        assertTrue(response.contains("date:"));
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
                .setBaseUrl("https://httpbin.org")
                .setPath("/post")
                .setBody(new ByteArrayInputStream("form=value".getBytes()))
                .build())
            .get();

        assertEquals(2, outputs.size());
        assertEquals("""
            -> POST https://httpbin.org/post
                        
            form=value
            -> https://httpbin.org/post
            """, outputs.get(0));
        var response = outputs.get(1);
        assertTrue(response.startsWith("""
            <- 200 https://httpbin.org/post
                        
            """));
        assertTrue(response.endsWith("""
            <- https://httpbin.org/post
            """));
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
                .setBaseUrl("https://httpbin.org")
                .setPath("/get")
                .build())
            .get();

        assertEquals(1, outputs.size());
        assertEquals("""
            -> GET https://httpbin.org/get
            -> https://httpbin.org/get
            <- 200 https://httpbin.org/get
            <- https://httpbin.org/get
            """, outputs.get(0));
    }
}