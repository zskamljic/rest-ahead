package io.github.zskamljic.restahead.processor;

import org.junit.jupiter.api.Test;

class PathProcessorTest extends CommonProcessorTest {
    @Test
    void interfaceWithInvalidPathFailsToCompile() {
        commonCompilationAssertion("path/InvalidPath.java")
            .failsToCompile()
            .withErrorContaining("path");
    }
}
