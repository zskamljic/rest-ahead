package com.lablizards.restahead.modeling.declaration;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public record BodyDeclaration(
    VariableElement element,
    String parameterName,
    List<TypeMirror> convertExceptions
) {
}
