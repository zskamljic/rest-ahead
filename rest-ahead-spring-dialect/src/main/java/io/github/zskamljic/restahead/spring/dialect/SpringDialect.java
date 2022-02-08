package io.github.zskamljic.restahead.spring.dialect;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.client.requests.parts.FilePart;
import io.github.zskamljic.restahead.encoding.MultiPartParameter;
import io.github.zskamljic.restahead.modeling.declaration.BodyParameter;
import io.github.zskamljic.restahead.modeling.parameters.ParameterWithExceptions;
import io.github.zskamljic.restahead.modeling.parameters.PartData;
import io.github.zskamljic.restahead.modeling.parameters.RequestParameter;
import io.github.zskamljic.restahead.polyglot.Dialect;
import io.github.zskamljic.restahead.request.BasicRequestLine;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents the spring dialect for RestAhead.
 */
public class SpringDialect implements Dialect {
    private static final List<Class<? extends Annotation>> BODY_ANNOTATIONS = List.of(
        RequestBody.class, RequestPart.class
    );
    private static final List<Class<? extends Annotation>> REQUEST_ANNOTATIONS = List.of(
        RequestHeader.class, PathVariable.class, RequestParam.class
    );
    private static final List<Class<? extends Annotation>> VERB_ANNOTATIONS = List.of(
        DeleteMapping.class, GetMapping.class, PatchMapping.class, PostMapping.class, PutMapping.class, RequestMapping.class
    );
    private static final List<Class<? extends Annotation>> ALL_ANNOTATIONS = Stream.concat(
        BODY_ANNOTATIONS.stream(), Stream.concat(REQUEST_ANNOTATIONS.stream(), VERB_ANNOTATIONS.stream())
    ).toList();

    @Override
    public List<Class<? extends Annotation>> allAnnotations() {
        return ALL_ANNOTATIONS;
    }

    @Override
    public List<Class<? extends Annotation>> requestAnnotations() {
        return REQUEST_ANNOTATIONS;
    }

    @Override
    public List<Class<? extends Annotation>> bodyAnnotations() {
        return BODY_ANNOTATIONS;
    }

    @Override
    public List<Class<? extends Annotation>> verbAnnotations() {
        return VERB_ANNOTATIONS;
    }

    @Override
    public Optional<BasicRequestLine> getRequestLine(Annotation annotation) {
        Verb verb;
        String path;
        if (annotation instanceof DeleteMapping deleteMapping) {
            verb = Verb.DELETE;
            path = deleteMapping.value()[0];
        } else if (annotation instanceof GetMapping getMapping) {
            verb = Verb.GET;
            path = getMapping.value()[0];
        } else if (annotation instanceof PatchMapping patchMapping) {
            verb = Verb.PATCH;
            path = patchMapping.value()[0];
        } else if (annotation instanceof PostMapping postMapping) {
            verb = Verb.POST;
            path = postMapping.value()[0];
        } else if (annotation instanceof PutMapping putMapping) {
            verb = Verb.PUT;
            path = putMapping.value()[0];
        } else if (annotation instanceof RequestMapping requestMapping) {
            var requestVerb = getVerb(requestMapping.method());
            if (requestVerb.isEmpty()) return Optional.empty();

            verb = requestVerb.get();
            path = requestMapping.value()[0];
        } else {
            return Optional.empty();
        }
        return Optional.of(new BasicRequestLine(verb, path));
    }

    @Override
    public Optional<RequestParameter> extractRequestAnnotation(Annotation annotation) {
        RequestParameter.Type type;
        String value;
        if (annotation instanceof RequestHeader header) {
            type = RequestParameter.Type.HEADER;
            value = header.value();
        } else if (annotation instanceof RequestParam query) {
            type = RequestParameter.Type.QUERY;
            value = query.value();
        } else if (annotation instanceof PathVariable path) {
            type = RequestParameter.Type.PATH;
            value = path.value();
        } else {
            return Optional.empty();
        }
        return Optional.of(new RequestParameter(type, value));
    }

    @Override
    public Optional<PartData> extractPart(List<? extends Annotation> bodyAnnotations) {
        return bodyAnnotations.stream()
            .filter(RequestPart.class::isInstance)
            .map(RequestPart.class::cast)
            .map(part -> new PartData(part.value()))
            .findFirst();
    }

    @Override
    public Optional<ParameterWithExceptions> createBodyPart(
        Elements elements,
        Types types,
        BodyParameter body,
        TypeMirror type
    ) {
        var multiPartType = elements.getTypeElement(MultipartFile.class.getName()).asType();
        if (!types.isSameType(type, multiPartType)) return Optional.empty();

        var part = new MultiPartParameter(
            body.httpName(),
            body.name() + ".getOriginalFilename()",
            Optional.of(FilePart.class),
            Optional.of(body.name() + ".getInputStream()")
        );
        var exceptions = elements.getTypeElement(IOException.class.getName()).asType();
        return Optional.of(new ParameterWithExceptions(part, Set.of(exceptions)));
    }

    /**
     * Used to map {@link RequestMethod} to {@link Verb}. If more than one is provided no verb is returned.
     *
     * @param methods the methods to check
     * @return the verb if applicable, empty otherwise
     */
    private Optional<Verb> getVerb(RequestMethod[] methods) {
        if (methods.length != 1) return Optional.empty();

        var verb = switch (methods[0]) {
            case DELETE -> Verb.DELETE;
            case GET -> Verb.GET;
            case PATCH -> Verb.PATCH;
            case POST -> Verb.POST;
            case PUT -> Verb.PUT;
            default -> null;
        };
        return Optional.ofNullable(verb);
    }
}
