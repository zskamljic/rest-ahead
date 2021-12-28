package com.lablizards.restahead.requests;

import com.lablizards.restahead.requests.parameters.RequestParameter;
import com.lablizards.restahead.requests.parameters.RequestParameterSpec;
import com.lablizards.restahead.requests.request.PresetQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Parameter holder.
 *
 * @param parameters all parameters of the function
 * @param headers    the header parameters
 * @param query      the query parameters
 */
public record RequestParameters(
    List<RequestParameter> parameters,
    List<RequestParameterSpec> headers,
    List<RequestParameterSpec> query,
    List<PresetQuery> presetQueries
) {
    public RequestParameters(
        List<RequestParameter> parameters,
        List<RequestParameterSpec> headers,
        List<RequestParameterSpec> query
    ) {
        this(parameters, headers, query, new ArrayList<>());
    }
}
