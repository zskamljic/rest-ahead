package io.github.zskamljic.restahead.modeling.declaration;

import io.github.zskamljic.restahead.modeling.conversion.Conversion;

import java.util.Optional;

/**
 * The return type specification.
 *
 * @param targetConversion the target type to convert to (the T in Future&lt;T&gt;)
 * @param adapterCall      the adapter class and method to use
 */
public record ReturnDeclaration(
    Optional<Conversion> targetConversion,
    Optional<ReturnAdapterCall> adapterCall
) {
}
