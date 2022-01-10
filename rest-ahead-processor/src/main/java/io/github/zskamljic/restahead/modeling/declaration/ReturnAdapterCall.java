package io.github.zskamljic.restahead.modeling.declaration;

/**
 * The type and method that should be called for a return value.
 *
 * @param adapterClass  the type containing the function
 * @param adapterMethod the method to call
 */
public record ReturnAdapterCall(
    AdapterClassDeclaration adapterClass,
    AdapterMethodDeclaration adapterMethod
) {
}
