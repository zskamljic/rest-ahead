package com.lablizards.restahead.processor;

import org.junit.jupiter.api.Test;

class ResponseProcessorTest extends CommonProcessorTest {
    @Test
    void interfaceWithResponseCompiles() {
        commonCompilationAssertion("response/ServiceWithResponse.java")
            .compilesWithoutWarnings();
    }

    @Test
    void interfaceWithUnknownResponseFailsToCompile() {
        commonCompilationAssertion("response/ServiceWithUnknownResponse.java")
            .failsToCompile()
            .withErrorContaining("Convert type")
            .and()
            .withErrorContaining("not supported");
    }
}
