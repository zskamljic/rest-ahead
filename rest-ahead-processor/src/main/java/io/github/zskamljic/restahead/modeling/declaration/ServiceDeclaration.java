package io.github.zskamljic.restahead.modeling.declaration;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;

/**
 * The service representation based on the interface
 *
 * @param packageName       the package name where the class should be generated
 * @param serviceType       the type to implement
 * @param calls             the calls that need to be implemented
 * @param requiresConverter if any of the calls in generated code need conversion
 */
public record ServiceDeclaration(
    String packageName,
    TypeElement serviceType,
    List<CallDeclaration> calls,
    boolean requiresConverter
) {
    private static final String GENERATED_SUFFIX = "$Impl";

    /**
     * Get the name of the generated class
     *
     * @return the name of the generated class
     */
    public String generatedName() {
        return serviceType.getSimpleName().toString() + GENERATED_SUFFIX;
    }

    /**
     * Collects all the required adapters for this service.
     *
     * @return a list of required adapters
     */
    public List<AdapterClassDeclaration> requiredAdapters() {
        return calls.stream()
            .map(CallDeclaration::returnDeclaration)
            .map(ReturnDeclaration::adapterCall)
            .flatMap(Optional::stream)
            .map(ReturnAdapterCall::adapterClass)
            .distinct()
            .toList();
    }
}
