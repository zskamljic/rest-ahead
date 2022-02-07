package io.github.zskamljic.restahead.processor;

import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

class RequestsProcessorTest extends CommonProcessorTest {
    @Test
    void generateServiceSucceeds() {
        commonCompilationAssertion("ValidService.java")
            .compilesWithoutWarnings();
    }

    @Test
    void generateServiceFailsForAbstractClass() {
        commonCompilationAssertion("basic/MethodClass.java")
            .failsToCompile()
            .withErrorContaining("interfaces");
    }

    @Test
    void interfaceWithDefaultFailsToCompile() {
        commonCompilationAssertion("basic/MethodService.java")
            .failsToCompile()
            .withErrorContaining("abstract");
    }

    @Test
    void classWithMethodFailsToCompile() {
        commonCompilationAssertion("basic/NormalClassMethod.java")
            .failsToCompile()
            .withErrorContaining("abstract");
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