package io.github.zskamljic.restahead.modeling.declaration;

import io.github.zskamljic.restahead.requests.request.PresetQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parameter holder.
 *
 * @param headers       the header parameters
 * @param query         the query parameters
 * @param paths         the path parameters
 * @param body          the body declaration
 * @param presetQueries the queries present in request line
 */
public record ParameterDeclaration(
    List<RequestParameterSpec> headers,
    List<RequestParameterSpec> query,
    List<RequestParameterSpec> paths,
    Optional<BodyDeclaration> body,
    List<PresetQuery> presetQueries
) {
    public ParameterDeclaration(
        List<RequestParameterSpec> headers,
        List<RequestParameterSpec> query,
        List<RequestParameterSpec> paths,
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<BodyDeclaration> body
    ) {
        this(headers, query, paths, body, new ArrayList<>());
    }
}
