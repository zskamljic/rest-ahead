package io.github.zskamljic.restahead.processor.stock;

import io.github.zskamljic.restahead.processor.CommonProcessorTest;
import org.junit.jupiter.api.Test;

class ParametersProcessorTest extends CommonProcessorTest {
    @Test
    void interfaceWithInvalidPathFailsToCompile() {
        commonCompilationAssertion("parameters/Parameters.java")
            .failsToCompile()
            .withErrorContaining("Exactly one annotation expected");
    }
}
