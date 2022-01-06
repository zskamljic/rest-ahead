package com.lablizards.restahead.modeling;

import com.lablizards.restahead.modeling.declaration.AdapterClassDeclaration;
import com.lablizards.restahead.modeling.declaration.CallDeclaration;
import com.lablizards.restahead.modeling.declaration.ServiceDeclaration;
import com.lablizards.restahead.requests.VerbMapping;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.NoType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Used to collect annotated methods together with their declaring class.
 */
public class ServiceModeler {
    private final Messager messager;
    private final Elements elements;
    private final MethodModeler methodModeler;

    public ServiceModeler(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.elements = elements;
        methodModeler = new MethodModeler(messager, elements, types);
    }

    /**
     * Explores the services and maps them to their declaring classes.
     *
     * @param annotations the annotations for which the code should be attributed
     * @param roundEnv    the environment from which to extrac the data
     * @param adapters    the adapters that can be discovered
     * @return service declarations discovered
     */
    public List<ServiceDeclaration> collectServices(
        Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv,
        List<AdapterClassDeclaration> adapters
    ) {
        var declaringElements = new HashMap<TypeElement, List<ExecutableElement>>();
        for (var annotation : annotations) {
            var annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            findDeclaringElements(annotation, annotatedElements, declaringElements);
        }

        if (hasInvalidDeclarations(declaringElements)) return List.of();

        return declaringElements.entrySet()
            .stream()
            .flatMap(entry -> createServiceDeclaration(entry.getKey(), entry.getValue(), adapters).stream())
            .toList();
    }

    /**
     * Checks if there's any invalid declarations present in the list.
     *
     * @param declaringElements the classes
     * @return if any errors are present or not
     */
    private boolean hasInvalidDeclarations(Map<TypeElement, List<ExecutableElement>> declaringElements) {
        var hasInvalid = false;
        for (var entry : declaringElements.entrySet()) {
            var functions = entry.getKey()
                .getEnclosedElements()
                .stream()
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .toList();
            for (var function : functions) {
                var annotation = VerbMapping.ANNOTATION_VERBS.stream()
                    .map(function::getAnnotation)
                    .filter(Objects::nonNull)
                    .toList();
                if (annotation.isEmpty()) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Function has no HTTP verb annotation", function);
                    hasInvalid = true;
                }
                if (annotation.size() != 1) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Exactly one HTTP verb annotation should be present", function);
                }
            }
        }
        return hasInvalid;
    }

    /**
     * Creates a service declaration based on type, functions and required adapters
     *
     * @param typeElement the supertype of the service
     * @param functions   the functions present in the interface
     * @param adapters    all available adapters
     * @return service declaration if no errors were found, empty otherwise
     */
    private Optional<ServiceDeclaration> createServiceDeclaration(
        TypeElement typeElement,
        List<ExecutableElement> functions,
        List<AdapterClassDeclaration> adapters
    ) {
        var calls = functions.stream()
            .map(function -> methodModeler.getCallDeclaration(function, adapters))
            .flatMap(Optional::stream)
            .toList();

        // There were errors in creation of the service declaration
        if (calls.size() != functions.size()) return Optional.empty();

        var servicePackage = elements.getPackageOf(typeElement);
        var packageName = servicePackage.isUnnamed() ? "" : servicePackage.getQualifiedName().toString();
        return Optional.of(new ServiceDeclaration(
            packageName,
            typeElement,
            calls,
            calls.stream()
                .anyMatch(CallDeclaration::requiresConverter)
        ));
    }

    /**
     * Finds the declaring elements for given annotation. Reports errors to messager if element is:
     * - not a method (this should be a compile error, as annotations cannot be applied to anything else)
     * - method is not abstract (interface method)
     * - method is not part of a class (should never happen)
     * - method is not declared in interface (declared in class for example)
     *
     * @param annotation        the annotation to which elements belong
     * @param annotatedElements the elements belonging to annotation
     * @param declaringElements the map where found where interface/method declarations should be stored
     */
    private void findDeclaringElements(
        TypeElement annotation,
        Set<? extends Element> annotatedElements,
        Map<TypeElement, List<ExecutableElement>> declaringElements
    ) {
        for (var element : annotatedElements) {
            if (!(element instanceof ExecutableElement executableElement)) {
                messager.printMessage(Diagnostic.Kind.ERROR, annotation + " can only be used on methods.", element);
                continue;
            }

            var modifiers = executableElement.getModifiers();
            if (!modifiers.contains(Modifier.ABSTRACT)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Only abstract methods can be generated.", element);
                continue;
            }

            var parent = executableElement.getEnclosingElement();
            if (!(parent instanceof TypeElement declaringType)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Only methods in classes can be generated", element);
                continue;
            }

            if (!(declaringType.getSuperclass() instanceof NoType)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Only interfaces support code generation at this time", element);
                continue;
            }

            declaringElements.compute(declaringType, (typeElement, executableElements) -> {
                if (executableElements == null) {
                    executableElements = new ArrayList<>();
                }
                executableElements.add(executableElement);
                return executableElements;
            });
        }
    }
}
