package io.github.zskamljic.restahead.processor.stock;

import com.google.testing.compile.JavaFileObjects;
import io.github.zskamljic.restahead.processor.CommonProcessorTest;
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
            .withErrorContaining("Invalid annotation combination");
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
            .generatesSources(
                JavaFileObjects.forResource("parameters/FormOnRecord$Impl.java"),
                JavaFileObjects.forResource("parameters/record/FormConverter.java")
            );
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
            .generatesSources(
                JavaFileObjects.forResource("parameters/FormOnClass$Impl.java"),
                JavaFileObjects.forResource("parameters/class/FormConverter.java")
            );
    }

    @Test
    void invalidClassFailsToCompile() {
        commonCompilationAssertion("parameters/FormOnClassInvalid.java")
            .failsToCompile()
            .withErrorContaining("not supported");
    }

    @Test
    void invalidFormNameFailsToCompile() {
        commonCompilationAssertion("parameters/FormOnWithWrongField.java")
            .failsToCompile()
            .withErrorContaining("Form name cannot be empty");
    }

    @Test
    void formAndPartOnRequestFailToCompile() {
        commonCompilationAssertion("parameters/FormAndPartSameField.java")
            .failsToCompile()
            .withErrorContaining("Request can't be both multipart and form encoded");
    }

    @Test
    void formWithPartCompiles() {
        commonCompilationAssertion("parameters/FormWithPart.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("parameters/FormWithPart$Impl.java"));
    }

    @Test
    void formWithPartOnInvalidTypeFailsToCompile() {
        commonCompilationAssertion("parameters/FormWithPartInvalidType.java")
            .failsToCompile()
            .withErrorContaining("Type is not supported");
    }
}
