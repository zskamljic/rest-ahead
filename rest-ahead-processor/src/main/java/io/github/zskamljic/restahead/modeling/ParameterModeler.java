package io.github.zskamljic.restahead.modeling;

import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.form.Part;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.request.Path;
import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.client.requests.parts.FieldPart;
import io.github.zskamljic.restahead.client.requests.parts.FilePart;
import io.github.zskamljic.restahead.encoding.BodyEncoding;
import io.github.zskamljic.restahead.encoding.ConvertBodyEncoding;
import io.github.zskamljic.restahead.encoding.FormBodyEncoding;
import io.github.zskamljic.restahead.encoding.MultiPartBodyEncoding;
import io.github.zskamljic.restahead.encoding.MultiPartParameter;
import io.github.zskamljic.restahead.encoding.generation.GenerationStrategy;
import io.github.zskamljic.restahead.modeling.declaration.BodyParameter;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.RequestParameterSpec;
import io.github.zskamljic.restahead.modeling.validation.HeaderValidator;
import io.github.zskamljic.restahead.modeling.validation.PathValidator;
import io.github.zskamljic.restahead.modeling.validation.QueryValidator;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Used to extract parameter info from the declaration.
 */
public class ParameterModeler {
    private static final List<Class<? extends Annotation>> REQUEST_ANNOTATIONS = List.of(
        Header.class, Path.class, Query.class
    );
    private static final List<Class<? extends Annotation>> BODY_ANNOTATIONS = List.of(
        Body.class, FormUrlEncoded.class, Part.class
    );

    private final Messager messager;
    private final PathValidator pathValidator;
    private final Elements elements;
    private final Types types;
    private final HeaderValidator headerValidator;
    private final QueryValidator queryValidator;
    private final TypeMirror ioException;

    public ParameterModeler(Messager messager, Elements elements, Types types, PathValidator pathValidator) {
        this.messager = messager;
        this.pathValidator = pathValidator;
        this.elements = elements;
        this.types = types;
        headerValidator = new HeaderValidator(messager, elements, types);
        queryValidator = new QueryValidator(messager, elements, types);
        ioException = elements.getTypeElement(IOException.class.getCanonicalName())
            .asType();
    }

    /**
     * Extracts the parts, reporting errors if any parameter does not fit into the request.
     *
     * @param function   the function from which to get the parts
     * @param allowsBody if body can be present in this request
     */
    public ParameterDeclaration getMethodParameters(ExecutableElement function, boolean allowsBody) {
        var parameters = function.getParameters();

        var headers = new ArrayList<RequestParameterSpec>();
        var queries = new ArrayList<RequestParameterSpec>();
        var paths = new ArrayList<RequestParameterSpec>();
        var bodies = new ArrayList<BodyParameter>();

        for (var parameter : parameters) {
            var requestAnnotations = REQUEST_ANNOTATIONS.stream()
                .map(parameter::getAnnotation)
                .filter(Objects::nonNull)
                .toList();
            var bodyAnnotations = BODY_ANNOTATIONS.stream()
                .map(parameter::getAnnotation)
                .filter(Objects::nonNull)
                .toList();

            if (!requestAnnotations.isEmpty() && !bodyAnnotations.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Invalid annotation combination present on parameter", parameter);
                continue;
            }

            if (!requestAnnotations.isEmpty()) {
                handleRequestAnnotations(requestAnnotations, parameter, headers, queries, paths);
            } else if (!bodyAnnotations.isEmpty()) {
                handleBodyAnnotations(bodyAnnotations, parameter, bodies);
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "Missing annotation on parameter", parameter);
            }
        }

        if (!bodies.isEmpty() && !allowsBody) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Body is not allowed for this type of request.", function);
            bodies.clear();
        }

        if (hasInvalidBodies(bodies)) {
            bodies.forEach(
                body -> messager.printMessage(Diagnostic.Kind.ERROR, "Only one body is allowed per request.", body.parameter())
            );
        }
        var bodyDeclaration = createBodyDeclaration(bodies);

        return new ParameterDeclaration(headers, queries, paths, bodyDeclaration);
    }

    /**
     * Creates body encoding if necessary.
     *
     * @param bodies the bodies that need code generation
     * @return encoding if valid and present, empty otherwise
     */
    private Optional<BodyEncoding> createBodyDeclaration(ArrayList<BodyParameter> bodies) {
        if (bodies.isEmpty()) return Optional.empty();

        if (bodies.size() == 1) {
            var body = bodies.get(0);
            var parameter = body.parameter();
            var parameterName = parameter.getSimpleName().toString();
            if (body.type() == BodyParameter.Type.FORM) {
                var strategy = GenerationStrategy.select(messager, elements, types, parameter.asType());
                if (strategy.isEmpty()) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Form encoding for type " + parameter.asType() + " is not supported.", parameter);
                    return Optional.empty();
                }
                return Optional.of(new FormBodyEncoding(parameterName, strategy.get()));
            } else if (body.type() == BodyParameter.Type.CONVERT) {
                return Optional.of(new ConvertBodyEncoding(parameterName, List.of(ioException)));
            }
        }

        var typeValidator = new TypeValidator(elements, types);
        var partMap = new ArrayList<MultiPartParameter>();
        var exceptions = new HashSet<TypeMirror>();
        for (var body : bodies) {
            var type = body.parameter().asType();
            if (typeValidator.isFileType(type)) {
                exceptions.addAll(typeValidator.getPossibleException(type));
                partMap.add(new MultiPartParameter(body.httpName(), body.name(), Optional.of(FilePart.class)));
            } else if (!typeValidator.isUnsupportedType(type)) {
                partMap.add(new MultiPartParameter(body.httpName(), body.name(), Optional.of(FieldPart.class)));
            } else if (typeValidator.isDirectMultipartType(type)) {
                partMap.add(new MultiPartParameter(body.httpName(), body.name(), Optional.empty()));
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "Type is not supported for multipart body.", body.parameter());
            }
        }
        return Optional.of(new MultiPartBodyEncoding(partMap, exceptions.stream().toList()));
    }

    /**
     * Handles the annotations on function that are relevant to query, path or headers.
     *
     * @param requestAnnotations the annotations present
     * @param parameter          the parameter on which the annotations are
     * @param headers            a list to collect headers in
     * @param queries            a list to collect queries in
     * @param paths              a list to collect paths in
     */
    private void handleRequestAnnotations(
        List<? extends Annotation> requestAnnotations,
        VariableElement parameter,
        List<RequestParameterSpec> headers,
        List<RequestParameterSpec> queries,
        List<RequestParameterSpec> paths
    ) {
        if (requestAnnotations.size() != 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Exactly one annotation expected on parameter", parameter);
            return;
        }
        var annotation = requestAnnotations.get(0);

        if (annotation instanceof Header header) {
            headerValidator.getHeaderSpec(header.value(), parameter).ifPresent(headers::add);
        } else if (annotation instanceof Query query) {
            queryValidator.getQuerySpec(query.value(), parameter).ifPresent(queries::add);
        } else if (annotation instanceof Path path) {
            pathValidator.getPathSpec(path.value(), parameter).ifPresent(paths::add);
        }
    }

    /**
     * Handle annotations relevant to the body.
     *
     * @param bodyAnnotations the annotations present on the parameter
     * @param parameter       the parameter we are processing
     * @param bodies          a list to collect bodies in
     */
    private void handleBodyAnnotations(
        List<? extends Annotation> bodyAnnotations,
        VariableElement parameter,
        List<BodyParameter> bodies
    ) {
        var hasFormUrlEncoded = bodyAnnotations.stream()
            .anyMatch(FormUrlEncoded.class::isInstance);
        var part = bodyAnnotations.stream()
            .filter(Part.class::isInstance)
            .map(Part.class::cast)
            .findFirst();
        if (hasFormUrlEncoded && part.isPresent()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Request can't be both multipart and form encoded", parameter);
            return;
        }

        var parameterName = parameter.getSimpleName().toString();
        if (part.isPresent()) {
            var partAnnotation = part.get();
            var value = partAnnotation.value();
            if (value.isBlank()) {
                value = parameterName;
            }
            bodies.add(new BodyParameter(parameter, value, parameterName, BodyParameter.Type.MULTIPART));
            return;
        }
        bodies.add(new BodyParameter(parameter, parameterName, parameterName, hasFormUrlEncoded ? BodyParameter.Type.FORM : BodyParameter.Type.CONVERT));
    }

    /**
     * Checks if the list has invalid body definitions.
     *
     * @param bodies the full list of bodies
     * @return true if any errors are found, false otherwise
     */
    private boolean hasInvalidBodies(ArrayList<BodyParameter> bodies) {
        if (bodies.size() <= 1) return false;

        return bodies.stream()
            .map(BodyParameter::type)
            .anyMatch(type -> type != BodyParameter.Type.MULTIPART);
    }
}
