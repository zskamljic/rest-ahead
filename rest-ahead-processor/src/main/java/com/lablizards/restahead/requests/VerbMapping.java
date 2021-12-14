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

import java.lang.annotation.Annotation;

/**
 * Utility to provide mappers for annotations, requests and specifications.
 */
public class VerbMapping {
    private VerbMapping() {
    }

    /**
     * Extracts {@link RequestSpec} from given annotation. Only HTTP verb annotations are supported.
     *
     * @param annotation the annotation from which to extract the verb and path
     * @return the request specification
     */
    public static RequestSpec annotationToVerb(Annotation annotation) {
        if (annotation instanceof Delete delete) {
            return new RequestSpec(DeleteRequest.class, delete.value());
        } else if (annotation instanceof Get get) {
            return new RequestSpec(GetRequest.class, get.value());
        } else if (annotation instanceof Patch patch) {
            return new RequestSpec(PatchRequest.class, patch.value());
        } else if (annotation instanceof Post post) {
            return new RequestSpec(PostRequest.class, post.value());
        } else if (annotation instanceof Put put) {
            return new RequestSpec(PutRequest.class, put.value());
        } else {
            throw new IllegalArgumentException("Annotation was not a valid verb: " + annotation);
        }
    }
}
