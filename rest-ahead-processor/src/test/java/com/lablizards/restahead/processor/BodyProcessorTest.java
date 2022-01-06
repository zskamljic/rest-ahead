package com.lablizards.restahead.processor;

import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

class BodyProcessorTest extends CommonProcessorTest {
    @Test
    void bodyInRequestsCompiles() {
        commonCompilationAssertion("parameters/BodyService.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("parameters/BodyService$Impl.java"));
    }
}
