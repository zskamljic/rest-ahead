package io.github.zskamljic.restahead.spring.dialect;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.polyglot.Dialect;
import io.github.zskamljic.restahead.request.BasicRequestLine;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * Represents the spring dialect for RestAhead.
 */
public class SpringDialect implements Dialect {
    private static final List<Class<? extends Annotation>> VERB_ANNOTATIONS = List.of(
        DeleteMapping.class, GetMapping.class, PatchMapping.class, PostMapping.class, PutMapping.class, RequestMapping.class
    );

    @Override
    public List<Class<? extends Annotation>> allAnnotations() {
        return VERB_ANNOTATIONS;
    }

    @Override
    public List<Class<? extends Annotation>> utilityAnnotations() {
        return List.of();
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
