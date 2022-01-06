package com.lablizards.restahead.modeling.declaration;

import com.lablizards.restahead.requests.request.PresetQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parameter holder.
 *
 * @param headers       the header parameters
 * @param query         the query parameters
 * @param body          the body declaration
 * @param presetQueries the queries present in request line
 */
public record ParameterDeclaration(
    List<RequestParameterSpec> headers,
    List<RequestParameterSpec> query,
    Optional<BodyDeclaration> body,
    List<PresetQuery> presetQueries
) {
    public ParameterDeclaration(
        List<RequestParameterSpec> headers,
        List<RequestParameterSpec> query,
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<BodyDeclaration> body
    ) {
        this(headers, query, body, new ArrayList<>());
    }
}
