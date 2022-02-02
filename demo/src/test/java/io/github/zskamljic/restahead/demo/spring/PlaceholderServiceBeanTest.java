package io.github.zskamljic.restahead.demo.spring;

import io.github.zskamljic.restahead.client.Client;
import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.conversion.Converter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class PlaceholderServiceBeanTest {
    @MockBean
    private Client mockClient;

    @MockBean
    private Converter mockConverter;

    @Autowired
    private ConfigCombinations.PlaceholderService placeholderService;

    @Test
    void placeHolderServiceInjectsBeans() {
        doReturn(CompletableFuture.completedFuture(new Response(200, Map.of(), InputStream.nullInputStream())))
            .when(mockClient).execute(any());

        var response = placeholderService.get();

        assertNotNull(response);
    }
}
