package io.github.zskamljic.restahead.demo.spring;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ConfigCombinationsTest {
    @Autowired
    private ConfigCombinations.ClientOnlyService clientOnlyService;

    @Autowired
    private ConfigCombinations.ClientAndInterceptorService clientAndInterceptorService;

    @Autowired
    private ConfigCombinations.InterceptorService interceptorService;

    @Autowired
    private ConfigCombinations.AdapterService adapterService;

    @Autowired
    private ConfigCombinations.PlaceholderService placeholderService;

    @AfterEach
    void tearDown() {
        DummyClient.requests.clear();
    }

    @Test
    void clientOnlyServiceHasClient() {
        clientOnlyService.get();
        var requests = DummyClient.requests;

        assertFalse(requests.isEmpty());
        var request = requests.get(0);
        assertTrue(((DummyClient) request.client()).getInterceptors().isEmpty());
    }

    @Test
    void clientAndInterceptorService() {
        clientAndInterceptorService.get();
        var requests = DummyClient.requests;

        assertFalse(requests.isEmpty());
        var request = requests.get(0);
        assertTrue(((DummyClient) request.client()).getInterceptors().get(0) instanceof ConfigCombinations.PassThroughInterceptor);
    }

    @Test
    void interceptorService() {
        interceptorService.get();
        var requests = DummyClient.requests;

        assertTrue(requests.isEmpty());
    }

    @Test
    void adapterService() {
        adapterService.get().get();
        var requests = DummyClient.requests;

        assertTrue(requests.isEmpty());
    }

    @Test
    void placeholderService() {
        var response = placeholderService.get();

        assertNotNull(response);
    }
}