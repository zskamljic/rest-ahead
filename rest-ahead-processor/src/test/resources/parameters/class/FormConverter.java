package io.github.zskamljic.restahead.generation;

import io.github.zskamljic.restahead.demo.FormOnClass;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.String;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class FormConverter {
    public static InputStream formEncode(FormOnClass.Sample value) {
        var stringValue = "first=" + URLEncoder.encode(String.valueOf(value.getFirst()), StandardCharsets.UTF_8) +
            "&smth=" + URLEncoder.encode(String.valueOf(value.getSecond()), StandardCharsets.UTF_8);
        return new ByteArrayInputStream(stringValue.getBytes());
    }
}