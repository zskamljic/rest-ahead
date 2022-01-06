package com.lablizards.restahead.processor;

import org.junit.jupiter.api.Test;

class AdapterProcessorTest extends CommonProcessorTest {
    @Test
    void adapterIsPickedUp() {
        commonCompilationAssertion("adapters/AdapterService.java")
            .compilesWithoutWarnings();
    }
}
