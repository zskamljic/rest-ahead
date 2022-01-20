package io.github.zskamljic.restahead.encoding;

import javax.lang.model.type.TypeMirror;
import java.util.List;

public record MultiPartBodyEncoding(
    List<MultiPartParameter> parts,
    List<TypeMirror> exceptions
) implements BodyEncoding {
}
