package io.github.zskamljic.restahead.processor.jaxrs;

import com.google.testing.compile.JavaFileObjects;
import io.github.zskamljic.restahead.processor.CommonProcessorTest;
import org.junit.jupiter.api.Test;

class JaxRsDialectTest extends CommonProcessorTest {
    @Test
    void processorGeneratesForJaxRs() {
        commonCompilationAssertion("jaxrs/JaxRsService.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("jaxrs/JaxRsService$Impl.java"));
    }
}
