package com.lablizards.restahead.demo.models;

public record AuthResponse(
    boolean authenticated,
    String token
) {
}
