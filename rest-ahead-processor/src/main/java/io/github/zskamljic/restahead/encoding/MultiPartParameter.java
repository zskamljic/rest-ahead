package io.github.zskamljic.restahead.encoding;

import io.github.zskamljic.restahead.client.requests.parts.MultiPart;

import java.util.Optional;

public record MultiPartParameter(
    String httpName,
    String name,
    Optional<Class<? extends MultiPart>> type
) {
}
