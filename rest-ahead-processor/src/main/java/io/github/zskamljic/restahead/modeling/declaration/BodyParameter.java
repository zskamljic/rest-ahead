package io.github.zskamljic.restahead.modeling.declaration;

import javax.lang.model.element.VariableElement;

public record BodyParameter(
    VariableElement parameter,
    String httpName,
    String name,
    Type type
) {
    public enum Type {
        CONVERT,
        FORM,
        MULTIPART
    }
}
