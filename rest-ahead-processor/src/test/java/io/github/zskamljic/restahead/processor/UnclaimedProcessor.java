package io.github.zskamljic.restahead.processor;

import io.github.zskamljic.restahead.annotations.Adapter;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.request.Path;
import io.github.zskamljic.restahead.annotations.request.Query;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Generated;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Claim other annotations to prevent compile warnings.
 */
public class UnclaimedProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(
            Adapter.class.getCanonicalName(),
            Body.class.getCanonicalName(),
            Generated.class.getCanonicalName(),
            Header.class.getCanonicalName(),
            Path.class.getCanonicalName(),
            Query.class.getCanonicalName()
        );
    }
}
