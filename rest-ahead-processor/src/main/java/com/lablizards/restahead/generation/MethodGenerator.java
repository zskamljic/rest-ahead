package com.lablizards.restahead.generation;

import com.lablizards.restahead.exceptions.RestException;
import com.lablizards.restahead.requests.RequestSpec;
import com.lablizards.restahead.requests.VerbMapping;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Used to generate methods annotated with HTTP annotations.
 */
public class MethodGenerator {
    private final Messager messager;
    private final ResponseConverterGenerator converterGenerator;

    /**
     * Create a new instance, reporting all data to given messager.
     *
     * @param messager the messager to report notes, errors etc. to
     */
    public MethodGenerator(Messager messager) {
        this.messager = messager;
        converterGenerator = new ResponseConverterGenerator(messager);
    }

    /**
     * Generate methods for given declarations.
     *
     * @param methodDeclarations the declarations with annotations for which to generate implementations.
     * @return the generated methods
     */
    public List<MethodSpec> generateMethods(List<ExecutableElement> methodDeclarations) {
        return methodDeclarations.stream()
            .map(this::createMethodSpec)
            .flatMap(Optional::stream)
            .toList();
    }

    /**
     * Attempts to extract a specification from the given function and generates a method based on that information.
     *
     * @param function the function to create an implementation for
     * @return Optional.empty() if extracting specification did not succeed, generated MethodSpec otherwise
     */
    private Optional<MethodSpec> createMethodSpec(ExecutableElement function) {
        return getRequestSpec(function).map(requestSpec -> generateMethodBody(function, requestSpec));
    }

    /**
     * Generates the method body for given function and specification
     *
     * @param function    the function to generate implementation for
     * @param requestSpec the specification fo the request
     * @return the generated function body
     */
    private MethodSpec generateMethodBody(ExecutableElement function, RequestSpec requestSpec) {
        var returnType = TypeName.get(function.getReturnType());

        var builder = MethodSpec.methodBuilder(function.getSimpleName().toString())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Override.class)
            .addStatement("var httpRequest = new $T($S)", requestSpec.request(), requestSpec.path())
            .beginControlFlow("try");

        if (returnType == TypeName.VOID) {
            builder.addStatement("client.execute(httpRequest)");
        } else {
            builder.addStatement("var response = client.execute(httpRequest)");
            converterGenerator.generateReturnStatement(returnType, builder, function);
        }

        return builder.nextControlFlow("catch ($T | $T exception)", IOException.class, InterruptedException.class)
            .addStatement("throw new $T(exception)", RestException.class)
            .endControlFlow()
            .returns(TypeName.get(function.getReturnType()))
            .build();
    }

    /**
     * Extract the verb, path etc. from annotated function. Errors are reported to messager.
     *
     * @param function the function from which to extract the specification
     * @return the specification if annotations are valid, Optional.empty() otherwise.
     */
    private Optional<RequestSpec> getRequestSpec(ExecutableElement function) {
        var presentAnnotations = VerbMapping.ANNOTATION_VERBS.stream()
            .map(function::getAnnotation)
            .filter(Objects::nonNull)
            .toList();

        if (presentAnnotations.size() != 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Exactly one verb annotation must be present on method", function);
            return Optional.empty();
        }

        var annotation = presentAnnotations.get(0);
        var requestSpec = VerbMapping.annotationToVerb(annotation);
        return Optional.of(requestSpec);
    }
}
