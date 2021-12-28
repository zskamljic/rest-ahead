package com.lablizards.restahead.processor;

import org.junit.jupiter.api.Test;

public class ParametersProcessorTest extends CommonProcessorTest {
    @Test
    void interfaceWithInvalidPathFailsToCompile() {
        commonCompilationAssertion("parameters/Parameters.java")
            .failsToCompile()
            .withErrorContaining("Exactly one annotation expected");
    }
}
