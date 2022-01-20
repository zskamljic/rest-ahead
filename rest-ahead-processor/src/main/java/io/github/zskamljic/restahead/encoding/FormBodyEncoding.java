package io.github.zskamljic.restahead.encoding;

import io.github.zskamljic.restahead.encoding.generation.GenerationStrategy;

/**
 * Specifies that the type should use form encoding.
 *
 * @param parameterName the name of parameter to encode
 * @param strategy      the strategy to use when generating the conversion code
 */
public record FormBodyEncoding(String parameterName, GenerationStrategy strategy) implements BodyEncoding {
}
