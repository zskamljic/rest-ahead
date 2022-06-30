package io.github.zskamljic.restahead.jaxrs.dialect;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.modeling.declaration.BodyParameter;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.parameters.ParameterWithExceptions;
import io.github.zskamljic.restahead.modeling.parameters.PartData;
import io.github.zskamljic.restahead.modeling.parameters.RequestParameter;
import io.github.zskamljic.restahead.polyglot.Dialect;
import io.github.zskamljic.restahead.request.BasicRequestLine;
import io.github.zskamljic.restahead.request.PresetValue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JaxRsDialect implements Dialect {
    @Override
    public List<Class<? extends Annotation>> allAnnotations() {
        var allAnnotations = new ArrayList<>(Dialect.super.allAnnotations());
        allAnnotations.add(Path.class);
        return allAnnotations;
    }

    @Override
    public List<Class<? extends Annotation>> parameterAnnotations() {
        return List.of(
            PathParam.class, QueryParam.class, HeaderParam.class
        );
    }

    @Override
    public List<Class<? extends Annotation>> requestAnnotations() {
        return List.of(
            Produces.class, Consumes.class
        );
    }

    @Override
    public List<Class<? extends Annotation>> bodyAnnotations() {
        return List.of();
    }

    @Override
    public List<Class<? extends Annotation>> verbAnnotations() {
        return List.of(
            DELETE.class, GET.class, HEAD.class, PATCH.class, POST.class, PUT.class
        );
    }

    @Override
    public Optional<BasicRequestLine> getRequestLine(ExecutableElement function, Annotation annotation) {
        Verb verb;
        if (annotation instanceof DELETE) {
            verb = Verb.DELETE;
        } else if (annotation instanceof GET) {
            verb = Verb.GET;
        } else if (annotation instanceof HEAD) {
            verb = Verb.HEAD;
        } else if (annotation instanceof PATCH) {
            verb = Verb.PATCH;
        } else if (annotation instanceof POST) {
            verb = Verb.POST;
        } else if (annotation instanceof PUT) {
            verb = Verb.PUT;
        } else {
            return Optional.empty();
        }

        var path = Optional.ofNullable(function.getAnnotation(Path.class))
            .map(Path::value)
            .orElse("");
        return Optional.of(new BasicRequestLine(verb, path));
    }

    @Override
    public Optional<RequestParameter> extractParameterAnnotation(Annotation annotation) {
        if (annotation instanceof PathParam pathParam) {
            return Optional.of(new RequestParameter(RequestParameter.Type.PATH, pathParam.value()));
        } else if (annotation instanceof QueryParam queryParam) {
            return Optional.of(new RequestParameter(RequestParameter.Type.QUERY, queryParam.value()));
        } else if (annotation instanceof HeaderParam headerParam) {
            return Optional.of(new RequestParameter(RequestParameter.Type.HEADER, headerParam.value()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PartData> extractPart(List<? extends Annotation> bodyAnnotations) {
        return Optional.empty();
    }

    @Override
    public Optional<ParameterWithExceptions> createBodyPart(Elements elements, Types types, BodyParameter body, TypeMirror type) {
        return Optional.empty();
    }

    @Override
    public void processRequestAnnotations(ExecutableElement function, ParameterDeclaration parameters) {
        Optional.ofNullable(function.getAnnotation(Consumes.class))
            .ifPresent(consumes -> parameters.presetHeaders().add(new PresetValue("Accept", String.join(", ", consumes.value()))));
        Optional.ofNullable(function.getAnnotation(Produces.class))
            .ifPresent(produces -> parameters.presetHeaders().add(new PresetValue("Content-Type", String.join(", ", produces.value()))));
    }
}
