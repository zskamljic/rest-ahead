package io.github.zskamljic.restahead.encoding;

import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Encoding for when type needs to be converted using external converters.
 *
 * @param exceptions the exceptions that can be thrown by the converter
 */
public record ConvertEncoding(List<TypeMirror> exceptions) implements Encoding {
}
