package io.github.zskamljic.restahead.processor.stock;

import com.google.testing.compile.JavaFileObjects;
import io.github.zskamljic.restahead.processor.CommonProcessorTest;
import org.junit.jupiter.api.Test;

class AdapterProcessorTest extends CommonProcessorTest {
    @Test
    void adapterIsPickedUp() {
        commonCompilationAssertion("adapters/AdapterService.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("adapters/AdapterService$Impl.java"));
    }
}
