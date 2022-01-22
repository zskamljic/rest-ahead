package io.github.zskamljic.restahead.processor;

import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

class ResponseProcessorTest extends CommonProcessorTest {
    @Test
    void interfaceWithResponseCompiles() {
        commonCompilationAssertion("response/ServiceWithResponse.java")
            .compilesWithoutWarnings();
    }

    @Test
    void interfaceWithUnknownResponseCompiles() {
        commonCompilationAssertion("response/ServiceWithUnknownResponse.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("response/ServiceWithUnknownResponse$Impl.java"));
    }

    @Test
    void interfaceWithGenericResponseCompiles() {
        commonCompilationAssertion("response/ServiceWithGenericResponse.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("response/ServiceWithGenericResponse$Impl.java"));
    }

    @Test
    void futureGenericResponseCompiles() {
        commonCompilationAssertion("response/FutureGenericResponse.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("response/FutureGenericResponse$Impl.java"));
    }

    @Test
    void responseBodyCompiles() {
        commonCompilationAssertion("response/ResponseWithBody.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("response/ResponseWithBody$Impl.java"));
    }

    @Test
    void responseBodyAndErrorCompiles() {
        commonCompilationAssertion("response/ResponseWithErrorBody.java")
            .compilesWithoutWarnings()
            .and()
            .generatesSources(JavaFileObjects.forResource("response/ResponseWithErrorBody$Impl.java"));
    }
}
