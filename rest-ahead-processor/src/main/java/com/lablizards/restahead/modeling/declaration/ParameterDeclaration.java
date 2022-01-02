package com.lablizards.restahead.modeling.declaration;

import com.lablizards.restahead.requests.request.PresetQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Parameter holder.
 *
 * @param headers       the header parameters
 * @param query         the query parameters
 * @param presetQueries the queries present in request line
 */
public record ParameterDeclaration(
    List<RequestParameterSpec> headers,
    List<RequestParameterSpec> query,
    List<PresetQuery> presetQueries
) {
    public ParameterDeclaration(
        List<RequestParameterSpec> headers,
        List<RequestParameterSpec> query
    ) {
        this(headers, query, new ArrayList<>());
    }
}
