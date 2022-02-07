package io.github.zskamljic.restahead.processor;

import org.junit.jupiter.api.Test;

class ParametersProcessorTest extends CommonProcessorTest {
    @Test
    void interfaceWithInvalidPathFailsToCompile() {
        commonCompilationAssertion("parameters/Parameters.java")
            .failsToCompile()
            .withErrorContaining("Exactly one annotation expected");
    }
}
