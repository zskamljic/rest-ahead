package io.github.zskamljic.restahead.encoding.generation;

import javax.annotation.processing.Messager;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Outlines the generation strategy for some type.
 */
public sealed interface FormConversionStrategy permits ClassGenerationStrategy, MapConversionStrategy, RecordGenerationStrategy {
    /**
     * The type that this strategy applies to. Return from this value will be used to ensure that only one converter
     * will be generated for each type.
     *
     * @return the type this converter applies to
     */
    TypeMirror type();

    /**
     * Selects an appropriate generation strategy for given type.
     *
     * @param messager the messager to report errors to
     * @param elements the elements to fetch type information from
     * @param types    the types utility to use for typing info
     * @param mirror   the type for which to find a strategy
     * @return the strategy or empty if none was found
     */
    static Optional<FormConversionStrategy> select(Messager messager, Elements elements, Types types, TypeMirror mirror) {
        Stream<OptionalStrategyProvider> providers = Stream.of(
            MapConversionStrategy::getIfSupported,
            RecordGenerationStrategy::getIfSupported,
            ClassGenerationStrategy::getIfSupported
        );
        return providers.map(provider -> provider.getIfSupported(messager, elements, types, mirror))
            .flatMap(Optional::stream)
            .findFirst();
    }

    /**
     * Common signature for static functions
     */
    @FunctionalInterface
    interface OptionalStrategyProvider {
        Optional<FormConversionStrategy> getIfSupported(Messager messager, Elements elements, Types types, TypeMirror mirror);
    }
}
