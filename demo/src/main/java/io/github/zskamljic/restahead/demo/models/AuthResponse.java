package io.github.zskamljic.restahead.demo.models;

public record AuthResponse(
    boolean authenticated,
    String token
) {
}
