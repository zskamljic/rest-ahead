package io.github.zskamljic.restahead.polyglot;

import io.github.zskamljic.restahead.annotations.form.FormName;
import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.form.Part;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.request.Headers;
import io.github.zskamljic.restahead.annotations.request.Path;
import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.annotations.verbs.Head;
import io.github.zskamljic.restahead.annotations.verbs.Patch;
import io.github.zskamljic.restahead.annotations.verbs.Post;
import io.github.zskamljic.restahead.annotations.verbs.Put;
import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.modeling.declaration.BodyParameter;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.parameters.ParameterWithExceptions;
import io.github.zskamljic.restahead.modeling.parameters.PartData;
import io.github.zskamljic.restahead.modeling.parameters.RequestParameter;
import io.github.zskamljic.restahead.request.BasicRequestLine;
import io.github.zskamljic.restahead.request.PresetValue;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The default dialect of RestAhead.
 */
public class RestAheadDialect implements Dialect {
    @Override
    public List<Class<? extends Annotation>> parameterAnnotations() {
        return List.of(
            Header.class, Path.class, Query.class
        );
    }

    @Override
    public List<Class<? extends Annotation>> requestAnnotations() {
        return List.of(
            Headers.class
        );
    }

    @Override
    public List<Class<? extends Annotation>> bodyAnnotations() {
        return List.of(
            Body.class, FormName.class, FormUrlEncoded.class, Part.class
        );
    }

    @Override
    public List<Class<? extends Annotation>> verbAnnotations() {
        return List.of(
            Delete.class, Get.class, Head.class, Patch.class, Post.class, Put.class
        );
    }

    @Override
    public Optional<BasicRequestLine> getRequestLine(ExecutableElement function, Annotation annotation) {
        Verb verb;
        String path;
        if (annotation instanceof Delete delete) {
            verb = Verb.DELETE;
            path = delete.value();
        } else if (annotation instanceof Get get) {
            verb = Verb.GET;
            path = get.value();
        } else if (annotation instanceof Head head) {
            verb = Verb.HEAD;
            path = head.value();
        } else if (annotation instanceof Patch patch) {
            verb = Verb.PATCH;
            path = patch.value();
        } else if (annotation instanceof Post post) {
            verb = Verb.POST;
            path = post.value();
        } else if (annotation instanceof Put put) {
            verb = Verb.PUT;
            path = put.value();
        } else {
            return Optional.empty();
        }
        return Optional.of(new BasicRequestLine(verb, path));
    }

    @Override
    public Optional<RequestParameter> extractParameterAnnotation(Annotation annotation) {
        RequestParameter.Type type;
        String value;
        if (annotation instanceof Header header) {
            type = RequestParameter.Type.HEADER;
            value = header.value();
        } else if (annotation instanceof Query query) {
            type = RequestParameter.Type.QUERY;
            value = query.value();
        } else if (annotation instanceof Path path) {
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
            .filter(Part.class::isInstance)
            .map(Part.class::cast)
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
        return Optional.empty();
    }

    @Override
    public void processRequestAnnotations(ExecutableElement function, ParameterDeclaration parameters) throws ProcessingException {
        var headers = function.getAnnotation(Headers.class);
        if (headers == null) return;

        var pattern = Pattern.compile("([\\w-]+): ([^\r\n]+)");
        var values = headers.value();
        for (var value : values) {
            if (value == null) throw new ProcessingException(function, "Header line must not be null");
            var matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                throw new ProcessingException(function, "Header line must match \"[\\w-]+: [^\\r\\n]+\" (Key: Value)");
            }
            parameters.presetHeaders().add(new PresetValue(matcher.group(1), matcher.group(2)));
        }
    }

    @Override
    public boolean hasFormAnnotation(List<? extends Annotation> bodyAnnotations) {
        return bodyAnnotations.stream()
            .anyMatch(FormUrlEncoded.class::isInstance);
    }
}
