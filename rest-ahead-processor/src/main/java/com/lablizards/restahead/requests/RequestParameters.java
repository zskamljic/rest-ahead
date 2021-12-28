package com.lablizards.restahead.requests;

import com.lablizards.restahead.requests.parameters.HeaderSpec;
import com.lablizards.restahead.requests.parameters.RequestParameter;

import java.util.List;

/**
 * Parameter holder.
 *
 * @param parameters all parameters of the function
 * @param headers    the header parameters
 */
public record RequestParameters(
    List<RequestParameter> parameters,
    List<HeaderSpec> headers
) {
}
