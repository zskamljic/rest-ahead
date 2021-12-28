package com.lablizards.restahead.requests;

import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.annotations.verbs.Get;
import com.lablizards.restahead.annotations.verbs.Patch;
import com.lablizards.restahead.annotations.verbs.Post;
import com.lablizards.restahead.annotations.verbs.Put;
import com.lablizards.restahead.client.requests.DeleteRequest;
import com.lablizards.restahead.client.requests.GetRequest;
import com.lablizards.restahead.client.requests.PatchRequest;
import com.lablizards.restahead.client.requests.PostRequest;
import com.lablizards.restahead.client.requests.PutRequest;
import com.lablizards.restahead.client.requests.Request;
import com.lablizards.restahead.requests.request.RequestLine;
import com.lablizards.restahead.requests.request.RequestSpec;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Utility to provide mappers for annotations, requests and specifications.
 */
public class VerbMapping {
    public static final List<Class<? extends Annotation>> ANNOTATION_VERBS = List.of(
        Delete.class,
        Get.class,
        Patch.class,
        Post.class,
        Put.class
    );

    private VerbMapping() {
    }

    /**
     * Extracts {@link RequestSpec} from given annotation. Only HTTP verb annotations are supported.
     *
     * @param annotation the annotation from which to extract the verb and path
     * @return the request specification
     */
    public static RequestLine annotationToVerb(Annotation annotation) {
        Class<? extends Request> request;
        String path;
        if (annotation instanceof Delete delete) {
            request = DeleteRequest.class;
            path = delete.value();
        } else if (annotation instanceof Get get) {
            request = GetRequest.class;
            path = get.value();
        } else if (annotation instanceof Patch patch) {
            request = PatchRequest.class;
            path = patch.value();
        } else if (annotation instanceof Post post) {
            request = PostRequest.class;
            path = post.value();
        } else if (annotation instanceof Put put) {
            request = PutRequest.class;
            path = put.value();
        } else {
            throw new IllegalArgumentException("Annotation was not a valid verb: " + annotation);
        }
        return new RequestLine(request, path);
    }
}
