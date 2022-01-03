package com.lablizards.restahead.modeling.declaration;

import javax.lang.model.type.TypeMirror;
import java.util.Optional;

/**
 * The return type specification.
 *
 * @param targetConversion the target type to convert to (the T in Future&lt;T&gt;)
 * @param adapterCall      the adapter class and method to use
 */
public record ReturnDeclaration(
    Optional<TypeMirror> targetConversion,
    Optional<ReturnAdapterCall> adapterCall
) {
}
