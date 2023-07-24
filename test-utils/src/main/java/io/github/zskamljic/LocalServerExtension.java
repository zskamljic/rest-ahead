package io.github.zskamljic;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LocalServerExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {
    private final LocalHttpBinServer server = new LocalHttpBinServer();
    private String url;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        url = "http://localhost:" + server.start();
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
        server.stop();
    }
}
