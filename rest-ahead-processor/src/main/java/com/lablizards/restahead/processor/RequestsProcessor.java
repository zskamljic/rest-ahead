package com.lablizards.restahead.processor;

import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;
import com.lablizards.restahead.generation.ServiceGenerator;
import com.lablizards.restahead.processing.ServiceCollector;
import com.lablizards.restahead.requests.VerbMapping;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Processor entry point for HTTP annotations.
 */
public class RequestsProcessor extends AbstractProcessor {
    private ServiceCollector serviceCollector;
    private ServiceGenerator serviceGenerator;

    /**
     * Initialize the implementation, extracting required fields from {@link ProcessingEnvironment}.
     *
     * @param processingEnv the environment from which to extract the required items from
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        var messager = processingEnv.getMessager();
        var filer = processingEnv.getFiler();
        var elementUtils = processingEnv.getElementUtils();
        serviceCollector = new ServiceCollector(messager);
        serviceGenerator = new ServiceGenerator(messager, filer, elementUtils);
    }

    /**
     * Process the given annotations from the environment.
     *
     * @param annotations the annotations found in code
     * @param roundEnv    the environment where classes are defined
     * @return true, to let compiler know we're the only one to process these annotations
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var classesWithMethods = serviceCollector.collectServices(annotations, roundEnv);
        classesWithMethods.forEach((key, value) -> serviceGenerator.generateService(key, value));
        return true;
    }

    /**
     * Always support latest version.
     *
     * @return the latest version supported
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * Returns the annotations that we generate code for.
     *
     * @return the set of annotations
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return VerbMapping.ANNOTATION_VERBS.stream()
            .map(Class::getCanonicalName)
            .collect(Collectors.toSet());
    }
}
