package io.github.zskamljic.restahead.processor.stock;

import com.google.testing.compile.JavaFileObjects;
import io.github.zskamljic.restahead.processor.CommonProcessorTest;
import org.junit.jupiter.api.Test;

class RequestsProcessorTest extends CommonProcessorTest {
    @Test
    void generateServiceSucceeds() {
        commonCompilationAssertion("ValidService.java")
            .compilesWithoutWarnings();
    }

    @Test
    void generateServiceIgnoresAbstractClass() {
        commonCompilationAssertion("basic/MethodClass.java")
            .compilesWithoutWarnings();
    }

    @Test
    void interfaceWithDefaultFailsToCompile() {
        commonCompilationAssertion("basic/MethodService.java")
            .failsToCompile()
            .withErrorContaining("Default methods in interfaces");
    }

    @Test
    void classWithMethodIgnoresClass() {
        commonCompilationAssertion("basic/NormalClassMethod.java")
            .compilesWithoutWarnings();
    }

    @Test
    void interfaceWithNonAnnotatedMethodFailsToCompile() {
        commonCompilationAssertion("basic/InterfaceWithNotAnnotatedMethod.java")
            .failsToCompile()
            .withErrorContaining("no HTTP verb annotation");
    }

    @Test
    void interfaceWithThrowsCompiles() {
        commonCompilationAssertion("basic/InterfaceWithThrows.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("basic/InterfaceWithThrows$Impl.java"));
    }

    @Test
    void interfaceWithMultipleAnnotationsFailsToCompile() {
        commonCompilationAssertion("basic/MultipleAnnotations.java")
            .failsToCompile()
            .withErrorContaining("Exactly one verb annotation must be present on method");
    }
}