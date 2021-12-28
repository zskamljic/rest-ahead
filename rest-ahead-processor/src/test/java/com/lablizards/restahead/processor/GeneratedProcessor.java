package com.lablizards.restahead.processor;

import com.lablizards.restahead.annotations.request.Header;
import com.lablizards.restahead.annotations.request.Query;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Generated;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Claim other annotations to prevent compile warnings.
 */
public class GeneratedProcessor extends AbstractProcessor {
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
            Generated.class.getCanonicalName(),
            Header.class.getCanonicalName(),
            Query.class.getCanonicalName()
        );
    }
}
