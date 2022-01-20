package io.github.zskamljic.restahead.encoding;

/**
 * Used to specify types of encoding.
 */
public sealed interface BodyEncoding permits ConvertBodyEncoding, FormBodyEncoding, MultiPartBodyEncoding {
}
