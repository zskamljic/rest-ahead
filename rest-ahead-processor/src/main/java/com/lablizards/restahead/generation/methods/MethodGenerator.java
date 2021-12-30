package com.lablizards.restahead.generation.methods;

import com.lablizards.restahead.exceptions.RestException;
import com.lablizards.restahead.generation.ResponseHandler;
import com.lablizards.restahead.requests.RequestParameters;
import com.lablizards.restahead.requests.VerbMapping;
import com.lablizards.restahead.requests.parameters.RequestParameterSpec;
import com.lablizards.restahead.requests.request.RequestLine;
import com.lablizards.restahead.requests.request.RequestSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Used to generate methods annotated with HTTP annotations.
 */
public class MethodGenerator {
    private final Messager messager;
    private final ResponseHandler responseHandler;
    private final PathValidator pathValidator;
    private final ParameterHandler parameterHandler;

    private final TypeMirror executionException;
    private final TypeMirror interruptedException;
    private final TypeMirror ioException;

    /**
     * Create a new instance, reporting all data to given messager.
     *
     * @param messager     the messager to report notes, errors etc. to
     * @param elementUtils the element utils to fetch type info from
     * @param types        the types utility
     */
    public MethodGenerator(Messager messager, Elements elementUtils, Types types) {
        this.messager = messager;
        responseHandler = new ResponseHandler(elementUtils);
        pathValidator = new PathValidator(messager);
        parameterHandler = new ParameterHandler(messager, elementUtils, types);
        executionException = elementUtils.getTypeElement(ExecutionException.class.getCanonicalName()).asType();
        interruptedException = elementUtils.getTypeElement(InterruptedException.class.getCanonicalName()).asType();
        ioException = elementUtils.getTypeElement(IOException.class.getCanonicalName()).asType();
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
        var declaredExceptions = function.getThrownTypes();
        var isCustomReturn = responseHandler.isCustomResponse(function.getReturnType());
        var missingExceptions = findMissingExceptions(isCustomReturn, declaredExceptions);

        var builder = MethodSpec.methodBuilder(function.getSimpleName().toString())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Override.class)
            .addExceptions(declaredExceptions.stream().map(TypeName::get).toList());

        addParametersToFunction(builder, requestSpec.parameters());

        var requestLine = requestSpec.requestLine();
        addRequestInitialization(builder, requestLine, requestSpec.parameters());
        if (!missingExceptions.isEmpty()) {
            builder.beginControlFlow("try");
        }

        var returnType = TypeName.get(function.getReturnType());
        if (returnType == TypeName.VOID) {
            builder.addStatement("client.execute(httpRequest).get()");
        } else {
            builder.addStatement("var response = client.execute(httpRequest).get()");
            responseHandler.generateReturnStatement(function.getReturnType(), builder);
        }

        if (!missingExceptions.isEmpty()) {
            if (missingExceptions.contains(executionException)) {
                builder.nextControlFlow("catch ($T exception)", ExecutionException.class)
                    .addStatement("throw new $T(exception.getCause())", RestException.class);
            }
            var remaining = missingExceptions.stream()
                .filter(Predicate.not(executionException::equals))
                .toList();
            if (!remaining.isEmpty()) {
                var exceptionsTemplate = remaining.stream()
                    .map(e -> "$T")
                    .collect(Collectors.joining(" | "));
                builder.nextControlFlow("catch (" + exceptionsTemplate + " exception)", remaining.toArray(Object[]::new))
                    .addStatement("throw new $T(exception)", RestException.class);
            }
            builder.endControlFlow();
        }
        return builder.returns(returnType)
            .build();
    }

    /**
     * Add request initialization lines.
     *
     * @param builder     the method builder
     * @param requestLine the request line info
     * @param parameters  the request parameters
     */
    private void addRequestInitialization(
        MethodSpec.Builder builder,
        RequestLine requestLine,
        RequestParameters parameters
    ) {
        builder.addStatement("var httpRequest = new $T($S)", requestLine.request(), requestLine.path());

        for (var header : parameters.headers()) {
            if (header.isIterable()) {
                builder.beginControlFlow("for (var headerItem : $L)", header.codeName());
                builder.addStatement("httpRequest.addHeader($S, $T.valueOf(headerItem))", header.httpName(), String.class);
                builder.endControlFlow();
            } else {
                builder.addStatement(
                    "httpRequest.addHeader($S, $T.valueOf($L))", header.httpName(), String.class, header.codeName()
                );
            }
        }
        for (var query : parameters.query()) {
            if (query.isIterable()) {
                builder.beginControlFlow("for (var headerItem : $L)", query.codeName());
                builder.addStatement("httpRequest.addQuery($S, $T.valueOf(headerItem))", query.httpName(), String.class);
                builder.endControlFlow();
            } else {
                builder.addStatement(
                    "httpRequest.addQuery($S, $T.valueOf($L))", query.httpName(), String.class, query.codeName()
                );
            }
        }
        for (var query : parameters.presetQueries()) {
            builder.addStatement(
                "httpRequest.addQuery($S, $S)", query.name(), query.value()
            );
        }
    }

    /**
     * Adds the parameters to the generated function
     *
     * @param builder    the method builder
     * @param parameters the parameters to add
     */
    private void addParametersToFunction(MethodSpec.Builder builder, RequestParameters parameters) {
        builder.addParameters(createParameters(parameters));
    }

    /**
     * Create {@link  RequestParameterSpec} instances for the given parameters
     *
     * @param parameters the parameters to create specs for
     * @return the generated specs
     */
    private List<ParameterSpec> createParameters(RequestParameters parameters) {
        return parameters.parameters()
            .stream()
            .map(parameterHandler::createParameter)
            .toList();
    }

    /**
     * Checks if {@link IOException} and {@link InterruptedException} are both declared.
     *
     * @param isCustomReturn     if the response type is converted
     * @param declaredExceptions the exceptions on template method
     * @return true if either of the exception is missing, false if all are present
     */
    private List<? extends TypeMirror> findMissingExceptions(boolean isCustomReturn, List<? extends TypeMirror> declaredExceptions) {
        var expectedExceptions = new ArrayList<>(List.of(executionException, interruptedException));
        if (isCustomReturn) {
            expectedExceptions.add(ioException);
        }
        return expectedExceptions.stream()
            .filter(Predicate.not(declaredExceptions::contains))
            .toList();
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

        var parameters = parameterHandler.getMethodParameters(function);

        var annotation = presentAnnotations.get(0);
        var requestLine = VerbMapping.annotationToVerb(annotation);
        return pathValidator.validatePathAndExtractQuery(function, requestLine, parameters);
    }
}
