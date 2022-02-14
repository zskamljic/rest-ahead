package io.github.zskamljic.restahead.processor;

import io.github.zskamljic.restahead.generation.ServiceGenerator;
import io.github.zskamljic.restahead.modeling.AdapterModeler;
import io.github.zskamljic.restahead.modeling.ServiceModeler;
import io.github.zskamljic.restahead.polyglot.Dialects;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Processor entry point for HTTP annotations.
 */
public class RequestsProcessor extends AbstractProcessor {
    private Messager messager;
    private Dialects dialects;
    private AdapterModeler adapterModeler;
    private ServiceGenerator serviceGenerator;
    private ServiceModeler serviceModeler;

    /**
     * Initialize the implementation, extracting required fields from {@link ProcessingEnvironment}.
     *
     * @param processingEnv the environment from which to extract the required items from
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        var filer = processingEnv.getFiler();
        var elements = processingEnv.getElementUtils();
        var types = processingEnv.getTypeUtils();
        dialects = new Dialects(messager);
        adapterModeler = new AdapterModeler(messager, elements, types);
        serviceGenerator = new ServiceGenerator(messager, filer);
        serviceModeler = new ServiceModeler(messager, elements, types, dialects);
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
        try {
            var adapters = adapterModeler.findAdapters(roundEnv);
            var verbs = filterVerbAnnotations(annotations);
            var serviceDeclarations = serviceModeler.collectServices(verbs, roundEnv, adapters);
            serviceDeclarations.forEach(service -> serviceGenerator.generateService(service));
        } catch (IllegalArgumentException e) {
            var stringWriter = new StringWriter();
            var printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            messager.printMessage(Diagnostic.Kind.ERROR, "Error when generating: " + stringWriter.getBuffer().toString());
        }
        return true;
    }

    /**
     * Filters discovered annotations to filter out only verb annotations.
     *
     * @param annotations the full set of annotations.
     * @return the filtered set
     */
    private Set<? extends TypeElement> filterVerbAnnotations(Set<? extends TypeElement> annotations) {
        return annotations.stream()
            .filter(dialects::isVerbAnnotation)
            .collect(Collectors.toSet());
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
        return dialects.supportedAnnotationTypes();
    }
}
