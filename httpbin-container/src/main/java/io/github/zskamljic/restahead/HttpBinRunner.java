package io.github.zskamljic.restahead;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.lang.reflect.Modifier;

public class HttpBinRunner implements BeforeAllCallback, AfterAllCallback {
    @Override
    public void beforeAll(@NonNull ExtensionContext context) {
        var container = (GenericContainer<?>) context.getStore(ExtensionContext.Namespace.GLOBAL).computeIfAbsent(getClass(),
            ignored -> new GenericContainer<>("mccutchen/go-httpbin:2.24.0")
                .withExposedPorts(8080)
                .waitingFor(Wait.forHttp("/get")));
        if (!container.isRunning()) {
            container.start();
        }
        var baseUrl = "http://" + container.getHost() + ":" + container.getFirstMappedPort();

        context.getTestClass().ifPresent(c -> setBaseUrl(c, baseUrl));
    }

    @Override
    public void afterAll(@NonNull ExtensionContext context) {
        var container = (GenericContainer<?>) context.getStore(ExtensionContext.Namespace.GLOBAL).remove(getClass());
        if (container != null) {
            container.stop();
        }
    }

    private void setBaseUrl(Class<?> testClass, String baseUrl) {
        for (var field : testClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(HttpBinUrl.class)) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    throw new RuntimeException("@HttpBinUrl annotated field must be static.");
                }
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new RuntimeException("@HttpBinUrl annotated field must not be final.");
                }
                field.setAccessible(true);
                try {
                    field.set(null, baseUrl);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
