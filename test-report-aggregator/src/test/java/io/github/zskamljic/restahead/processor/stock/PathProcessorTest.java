package io.github.zskamljic.restahead.processor.stock;

import com.google.testing.compile.JavaFileObjects;
import io.github.zskamljic.restahead.processor.CommonProcessorTest;
import org.junit.jupiter.api.Test;

class PathProcessorTest extends CommonProcessorTest {
    @Test
    void interfaceWithInvalidPathFailsToCompile() {
        commonCompilationAssertion("path/InvalidPath.java")
            .failsToCompile()
            .withErrorContaining("path");
    }

    @Test
    void pathAnnotationCompilesCorrectly() {
        commonCompilationAssertion("path/PathAnnotation.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("path/PathAnnotation$Impl.java"));
    }

    @Test
    void pathAnnotationCompilesWithParameterName() {
        commonCompilationAssertion("path/PathAnnotationCodeName.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("path/PathAnnotationCodeName$Impl.java"));
    }

    @Test
    void pathAnnotationWithoutPlaceholderFails() {
        commonCompilationAssertion("path/PathWithoutPlaceholder.java")
            .failsToCompile()
            .withErrorContaining("parts are present, but there are none expected");
    }

    @Test
    void pathAnnotationFailsForDuplicatePlaceholders() {
        commonCompilationAssertion("path/PathWithDuplicates.java")
            .failsToCompile()
            .withErrorContaining("duplicate");
    }

    @Test
    void pathWithIterableFailsToCompile() {
        commonCompilationAssertion("path/PathWithIterable.java")
            .failsToCompile()
            .withErrorContaining("singular");
    }
}
