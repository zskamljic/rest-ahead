package io.github.zskamljic.restahead.encoding;

import io.github.zskamljic.restahead.encoding.generation.GenerationStrategy;

/**
 * Specifies that the type should use form encoding.
 *
 * @param strategy the strategy to use when generating the conversion code
 */
public record FormEncoding(GenerationStrategy strategy) implements Encoding {
}
