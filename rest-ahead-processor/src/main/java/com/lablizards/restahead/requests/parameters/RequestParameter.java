package com.lablizards.restahead.requests.parameters;

import javax.lang.model.type.TypeMirror;

/**
 * Contains the data relevant to the function parameter
 *
 * @param type the type of the parameter
 * @param name the name of the parameter
 */
public record RequestParameter(
    TypeMirror type,
    String name
) {
}
