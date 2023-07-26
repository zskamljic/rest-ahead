package io.github.zskamljic;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LocalServerExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {
    private final GenericContainer<?> container = new GenericContainer<>("kennethreitz/httpbin");
    private String url;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        int port = 0;
        while (port == 0) {
            try (var socket = new ServerSocket(ThreadLocalRandom.current().nextInt(1025, 65536))) {
                port = socket.getLocalPort();
            } catch (IOException ignored) {
                // Port taken
            }
        }
        container.setPortBindings(List.of(port + ":80"));
        url = "http://localhost:" + port;
        container.start();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        for (var instance : extensionContext.getRequiredTestInstances().getAllInstances()) {
            var fields = instance.getClass().getDeclaredFields();
            for (var field : fields) {
                if (!field.isAnnotationPresent(LocalUrl.class)) continue;

                if (field.getType() != String.class) {
                    throw new IllegalArgumentException("LocalUrl field must be of type String");
                }

                field.setAccessible(true);
                field.set(instance, url);
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        container.stop();
    }
}
