package com.lablizards.restahead.processor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

class RequestsProcessorTest {
    @Test
    void generateServiceSucceeds() {
        var validSource = JavaFileObjects.forResource("ValidService.java");

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(List.of(validSource))
            .processedWith(new RequestsProcessor())
            .compilesWithoutError();
    }

    @Test
    void generateServiceFailsForAbstractClass() {
        var source = JavaFileObjects.forResource("MethodClass.java");

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(List.of(source))
            .processedWith(new RequestsProcessor())
            .failsToCompile()
            .withErrorContaining("interfaces");
    }

    @Test
    void interfaceWithDefaultFailsToCompile() {
        var source = JavaFileObjects.forResource("MethodService.java");

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(List.of(source))
            .processedWith(new RequestsProcessor())
            .failsToCompile()
            .withErrorContaining("abstract");
    }

    @Test
    void classWithMethodFailsToCompile() {
        var source = JavaFileObjects.forResource("NormalClassMethod.java");

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(List.of(source))
            .processedWith(new RequestsProcessor())
            .failsToCompile()
            .withErrorContaining("abstract");
    }

    @Test
    void interfaceWithNonAnnotatedMethodFailsToCompile() {
        var source = JavaFileObjects.forResource("InterfaceWithNotAnnotatedMethod.java");

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(List.of(source))
            .processedWith(new RequestsProcessor())
            .failsToCompile()
            .withErrorContaining("no HTTP verb annotation");
    }
}