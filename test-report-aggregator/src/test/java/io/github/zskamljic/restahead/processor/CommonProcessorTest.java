package io.github.zskamljic.restahead.processor;

import com.google.common.truth.Truth;
import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;

import javax.annotation.processing.AbstractProcessor;
import java.util.Arrays;

public abstract class CommonProcessorTest {
    private final AbstractProcessor requestProcessor = new RequestsProcessor();
    private final AbstractProcessor generatedProcessor = new UnclaimedProcessor();

    protected CompileTester commonCompilationAssertion(String... files) {
        var sources = Arrays.stream(files)
            .map(JavaFileObjects::forResource)
            .toList();

        return Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(sources)
            .processedWith(requestProcessor, generatedProcessor);
    }
}
