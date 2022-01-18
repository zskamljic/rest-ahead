package io.github.zskamljic.restahead.processor;

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

    @Test
    void formOnInvalidParam() {
        commonCompilationAssertion("parameters/FormOnInvalid.java")
            .failsToCompile()
            .withErrorContaining("@Body");
    }

    @Test
    void formOnValidMapCompiles() {
        commonCompilationAssertion("parameters/FormOnValidMap.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("parameters/FormOnValidMap$Impl.java"));
    }

    @Test
    void unsupportedMapFailsToCompile() {
        commonCompilationAssertion("parameters/FormOnInvalidMap.java")
            .failsToCompile()
            .withErrorContaining("not supported");
    }

    @Test
    void validRecordCompiles() {
        commonCompilationAssertion("parameters/FormOnRecord.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("parameters/FormOnRecord$Impl.java"));
    }

    @Test
    void invalidRecordFailsToCompile() {
        commonCompilationAssertion("parameters/FormOnRecordInvalid.java")
            .failsToCompile()
            .withErrorContaining("not supported");
    }

    @Test
    void validClassCompiles() {
        commonCompilationAssertion("parameters/FormOnClass.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("parameters/FormOnClass$Impl.java"));
    }

    @Test
    void invalidClassFailsToCompile() {
        commonCompilationAssertion("parameters/FormOnClassInvalid.java")
            .failsToCompile()
            .withErrorContaining("not supported");
    }
}
