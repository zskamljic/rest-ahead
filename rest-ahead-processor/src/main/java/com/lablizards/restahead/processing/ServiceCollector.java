package com.lablizards.restahead.processing;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.NoType;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used to collect annotated methods together with their declaring class.
 */
public class ServiceCollector {
    private final Messager messager;

    public ServiceCollector(Messager messager) {
        this.messager = messager;
    }

    /**
     * Explores the services and maps them to their declaring classes.
     *
     * @param annotations the annotations for which the code should be attributed
     * @param roundEnv    the environment from which to extrac the data
     * @return declared methods associated by their declaring class
     */
    public Map<TypeElement, List<ExecutableElement>> collectServices(
        Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv
    ) {
        var declaringElements = new HashMap<TypeElement, List<ExecutableElement>>();
        for (var annotation : annotations) {
            var annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            findDeclaringElements(annotation, annotatedElements, declaringElements);
        }
        return declaringElements;
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
