package io.github.zskamljic.restahead.processor.spring;

import com.google.testing.compile.JavaFileObjects;
import io.github.zskamljic.restahead.processor.CommonProcessorTest;
import org.junit.jupiter.api.Test;

class SpringDialectTest extends CommonProcessorTest {
    @Test
    void processorGeneratesForSpring() {
        commonCompilationAssertion("spring/SpringService.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("spring/SpringService$Impl.java"));
    }
}
