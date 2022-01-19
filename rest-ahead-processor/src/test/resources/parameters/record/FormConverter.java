package io.github.zskamljic.restahead.generation;

import io.github.zskamljic.restahead.demo.FormOnRecord;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.String;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class FormConverter {
    public static InputStream formEncode(FormOnRecord.Sample value) {
        var stringValue = "first=" + URLEncoder.encode(String.valueOf(value.first()), StandardCharsets.UTF_8) +
            "&2nd=" + URLEncoder.encode(String.valueOf(value.second()), StandardCharsets.UTF_8);
        return new ByteArrayInputStream(stringValue.getBytes());
    }
}