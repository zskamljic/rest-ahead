package com.lablizards.restahead.generation;

import com.lablizards.restahead.client.RestClient;
import com.lablizards.restahead.requests.VerbMapping;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Generated;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Used to generate implementation for services.
 */
public class ServiceGenerator {
    private final Messager messager;
    private final Filer filer;
    private final Elements elementUtils;
    private final MethodGenerator methodGenerator;

    /**
     * Default constructor.
     *
     * @param messager     the messager where info, errors etc. should be reported
     * @param filer        the filer to use when writing generated files
     * @param elementUtils utilities to retrieve misc data about Element
     */
    public ServiceGenerator(Messager messager, Filer filer, Elements elementUtils) {
        this.messager = messager;
        this.filer = filer;
        this.elementUtils = elementUtils;
        this.methodGenerator = new MethodGenerator(messager);
    }

    /**
     * Generate the service implementation.
     *
     * @param serviceDeclaration the interface declaration to conform to
     * @param methodDeclarations the methods declared in the interface
     */
    public void generateService(
        TypeElement serviceDeclaration,
        List<ExecutableElement> methodDeclarations
    ) {
        if (declarationInvalid(serviceDeclaration, methodDeclarations)) return;

        var methods = methodGenerator.generateMethods(methodDeclarations);

        var generatedAnnotation = createGeneratedAnnotation();

        var typeName = serviceDeclaration.getSimpleName().toString() + "$Impl";
        var type = generateTypeSpecification(typeName, serviceDeclaration, generatedAnnotation, methods);

        var servicePackage = elementUtils.getPackageOf(serviceDeclaration);
        var packageName = servicePackage.isUnnamed() ? "" : servicePackage.getQualifiedName().toString();
        var javaFile = JavaFile.builder(packageName, type)
            .build();

        try {
            javaFile.writeTo(filer);
            messager.printMessage(Diagnostic.Kind.NOTE, "Generated " + type.name);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Unable to write class: " + e.getMessage());
        }
    }

    /**
     * Checks if service declaration is not valid, such as methods missing annotations.
     *
     * @param serviceDeclaration the service to check
     * @param methodDeclarations the methods being processed
     * @return true if any issue is found, false otherwise
     */
    private boolean declarationInvalid(TypeElement serviceDeclaration, List<ExecutableElement> methodDeclarations) {
        var functions = serviceDeclaration.getEnclosedElements()
            .stream()
            .filter(element -> element instanceof ExecutableElement)
            .map(element -> ((ExecutableElement) element))
            .toList();
        var invalid = false;
        for (var function : functions) {
            var annotations = VerbMapping.ANNOTATION_VERBS.stream()
                .map(function::getAnnotation)
                .filter(Objects::nonNull)
                .toList();
            if (annotations.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Function has no HTTP verb annotation", function);
                invalid = true;
            }
        }
        return invalid;
    }

    /**
     * Generate the type specification.
     *
     * @param typeName            the name for generated service
     * @param serviceDeclaration  the interface containing annotated methods
     * @param generatedAnnotation the generated annotation to add to implementation
     * @param methods             the methods to attach to generated class
     * @return the generated class
     */
    private TypeSpec generateTypeSpecification(
        String typeName,
        TypeElement serviceDeclaration,
        AnnotationSpec generatedAnnotation,
        List<MethodSpec> methods
    ) {
        return TypeSpec.classBuilder(typeName)
            .addSuperinterface(serviceDeclaration.asType())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(generatedAnnotation)
            .addMethods(methods)
            .addField(createClientField())
            .addMethod(createConstructor())
            .build();
    }

    /**
     * Creates the @Generated annotation
     *
     * @return the generated annotation with a comment
     */
    private AnnotationSpec createGeneratedAnnotation() {
        return AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", "Generated by RestAhead")
            .build();
    }

    /**
     * Creates the client field.
     *
     * @return field specification for client
     */
    private FieldSpec createClientField() {
        return FieldSpec.builder(RestClient.class, "client", Modifier.PRIVATE, Modifier.FINAL)
            .build();
    }

    /**
     * Creates the constructor for the service.
     *
     * @return the constructor specification.
     */
    private MethodSpec createConstructor() {
        return MethodSpec.methodBuilder("<init>")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(RestClient.class, "client")
            .addStatement("this.client = client")
            .build();
    }
}
