package io.github.zskamljic.restahead.encoding;

import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Represents encoding for multipart bodies.
 *
 * @param parts      the parts of the body
 * @param exceptions the exceptions that can be thrown when constructing the body
 */
public record MultiPartBodyEncoding(
    List<MultiPartParameter> parts,
    List<TypeMirror> exceptions
) implements BodyEncoding {
}
