package com.lablizards.restahead.modeling.declaration;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * The method that can be used as an adapter.
 *
 * @param name              the name of the method
 * @param returnType        the output of this adapter
 * @param adapterParameters the parameters of the adapter
 * @param exceptions        the exceptions thrown by this adapter
 * @param executableElement the executable element itself
 */
public record AdapterMethodDeclaration(
    String name,
    TypeMirror returnType,
    List<DeclaredType> adapterParameters,
    List<TypeMirror> exceptions,
    ExecutableElement executableElement
) {
}
