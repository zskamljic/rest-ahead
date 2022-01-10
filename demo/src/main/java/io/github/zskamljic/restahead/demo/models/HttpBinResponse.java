package io.github.zskamljic.restahead.demo.models;

import java.util.Map;

public record HttpBinResponse(
    Map<String, String> args,
    String data,
    Map<String, Object> files,
    Map<String, Object> form,
    Map<String, String> headers,
    Map<String, Object> json,
    String origin,
    String url
) {
}
