package io.github.zskamljic.restahead.encoding.generation;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Outlines the generation strategy for some type.
 */
public sealed interface GenerationStrategy permits ClassGenerationStrategy, MapGenerationStrategy, RecordGenerationStrategy {
    /**
     * The type that this strategy applies to. Return from this value will be used to ensure that only one converter
     * will be generated for each type.
     *
     * @return the type this converter applies to
     */
    TypeMirror type();

    /**
     * Generate the method for this type and strategy.
     *
     * @return the generated convert method.
     */
    MethodSpec generateMethod();

    /**
     * Selects an appropriate generation strategy for given type.
     *
     * @param elements the elements to fetch type information from
     * @param types    the types utility to use for typing info
     * @param mirror   the type for which to find a strategy
     * @return the strategy or empty if none was found
     */
    static Optional<GenerationStrategy> select(Elements elements, Types types, TypeMirror mirror) {
        Stream<OptionalStrategyProvider> providers = Stream.of(
            MapGenerationStrategy::getIfSupported,
            RecordGenerationStrategy::getIfSupported,
            ClassGenerationStrategy::getIfSupported
        );
        return providers.map(provider -> provider.getIfSupported(elements, types, mirror))
            .flatMap(Optional::stream)
            .findFirst();
    }

    /**
     * Common signature for static functions
     */
    @FunctionalInterface
    interface OptionalStrategyProvider {
        Optional<GenerationStrategy> getIfSupported(Elements elements, Types types, TypeMirror mirror);
    }
}
