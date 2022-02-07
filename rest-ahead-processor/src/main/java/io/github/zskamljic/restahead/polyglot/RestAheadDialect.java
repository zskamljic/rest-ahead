package io.github.zskamljic.restahead.polyglot;

import io.github.zskamljic.restahead.annotations.Adapter;
import io.github.zskamljic.restahead.annotations.form.FormName;
import io.github.zskamljic.restahead.annotations.form.FormUrlEncoded;
import io.github.zskamljic.restahead.annotations.form.Part;
import io.github.zskamljic.restahead.annotations.request.Body;
import io.github.zskamljic.restahead.annotations.request.Header;
import io.github.zskamljic.restahead.annotations.request.Path;
import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.annotations.verbs.Delete;
import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.annotations.verbs.Patch;
import io.github.zskamljic.restahead.annotations.verbs.Post;
import io.github.zskamljic.restahead.annotations.verbs.Put;
import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.request.BasicRequestLine;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The default dialect of RestAhead.
 */
public class RestAheadDialect implements Dialect {
    private static final List<Class<? extends Annotation>> UTILITY_ANNOTATIONS = List.of(
        Adapter.class, Body.class, Header.class, Path.class, Query.class, FormName.class, FormUrlEncoded.class, Part.class
    );
    private static final List<Class<? extends Annotation>> VERB_ANNOTATIONS = List.of(
        Delete.class, Get.class, Patch.class, Post.class, Put.class
    );

    private final List<Class<? extends Annotation>> allAnnotations = Stream.concat(
        UTILITY_ANNOTATIONS.stream(),
        VERB_ANNOTATIONS.stream()
    ).toList();
    
    @Override
    public List<Class<? extends Annotation>> allAnnotations() {
        return allAnnotations;
    }

    @Override
    public List<Class<? extends Annotation>> utilityAnnotations() {
        return UTILITY_ANNOTATIONS;
    }

    @Override
    public List<Class<? extends Annotation>> verbAnnotations() {
        return VERB_ANNOTATIONS;
    }

    @Override
    public Optional<BasicRequestLine> getRequestLine(Annotation annotation) {
        Verb verb;
        String path;
        if (annotation instanceof Delete delete) {
            verb = Verb.DELETE;
            path = delete.value();
        } else if (annotation instanceof Get get) {
            verb = Verb.GET;
            path = get.value();
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
}
