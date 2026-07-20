package io.github.zskamljic.restahead.demo.models;

import java.util.List;
import java.util.Map;

public record HttpBinResponse(
    Map<String, List<String>> args,
    String data,
    Map<String, Object> files,
    Map<String, Object> form,
    Map<String, List<String>> headers,
    Map<String, Object> json,
    String origin,
    String url
) {
}
