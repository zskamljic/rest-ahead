package io.github.zskamljic.restahead.processor;

import io.github.zskamljic.restahead.generation.ServiceGenerator;
import io.github.zskamljic.restahead.modeling.AdapterModeler;
import io.github.zskamljic.restahead.modeling.ServiceModeler;
import io.github.zskamljic.restahead.requests.VerbMapping;

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
    private ServiceModeler serviceModeler;
    private ServiceGenerator serviceGenerator;
    private AdapterModeler adapterModeler;

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
        var elements = processingEnv.getElementUtils();
        var types = processingEnv.getTypeUtils();
        serviceModeler = new ServiceModeler(messager, elements, types);
        adapterModeler = new AdapterModeler(messager, elements, types);
        serviceGenerator = new ServiceGenerator(messager, filer);
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
        var adapters = adapterModeler.findAdapters(roundEnv);
        var serviceDeclarations = serviceModeler.collectServices(annotations, roundEnv, adapters);
        serviceDeclarations.forEach(serviceGenerator::generateService);
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