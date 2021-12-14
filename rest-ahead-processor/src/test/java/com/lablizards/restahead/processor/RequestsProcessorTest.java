package com.lablizards.restahead.processor;

import com.google.common.truth.Truth;
import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.AbstractProcessor;
import java.util.Arrays;

class RequestsProcessorTest {
    private final AbstractProcessor requestProcessor = new RequestsProcessor();
    private final AbstractProcessor generatedProcessor = new GeneratedProcessor();

    @Test
    void generateServiceSucceeds() {
        commonCompilationAssertion("ValidService.java")
            .compilesWithoutWarnings();
    }

    @Test
    void generateServiceFailsForAbstractClass() {
        commonCompilationAssertion("MethodClass.java")
            .failsToCompile()
            .withErrorContaining("interfaces");
    }

    @Test
    void interfaceWithDefaultFailsToCompile() {
        commonCompilationAssertion("MethodService.java")
            .failsToCompile()
            .withErrorContaining("abstract");
    }

    @Test
    void classWithMethodFailsToCompile() {
        commonCompilationAssertion("NormalClassMethod.java")
            .failsToCompile()
            .withErrorContaining("abstract");
    }

    @Test
    void interfaceWithNonAnnotatedMethodFailsToCompile() {
        commonCompilationAssertion("InterfaceWithNotAnnotatedMethod.java")
            .failsToCompile()
            .withErrorContaining("no HTTP verb annotation");
    }

    @Test
    void interfaceWithResponseCompiles() {
        commonCompilationAssertion("ServiceWithResponse.java")
            .compilesWithoutWarnings();
    }

    @Test
    void interfaceWithUnknownResponseFailsToCompile() {
        commonCompilationAssertion("ServiceWithUnknownResponse.java")
            .failsToCompile()
            .withErrorContaining("Convert type")
            .and()
            .withErrorContaining("not supported");
    }

    private CompileTester commonCompilationAssertion(String... files) {
        var sources = Arrays.stream(files)
            .map(JavaFileObjects::forResource)
            .toList();

        return Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(sources)
            .processedWith(requestProcessor, generatedProcessor);
    }
}