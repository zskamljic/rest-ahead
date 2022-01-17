package io.github.zskamljic.restahead.modeling.declaration;

import io.github.zskamljic.restahead.encoding.Encoding;

import javax.lang.model.element.VariableElement;

/**
 * Contains the body specification and required encoding strategy.
 *
 * @param element       the parameter that represents the body
 * @param parameterName the name of the parameter
 * @param encoding      the encoding to use for this body
 */
public record BodyDeclaration(
    VariableElement element,
    String parameterName,
    Encoding encoding
) {
}
