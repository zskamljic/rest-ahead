package io.github.zskamljic.restahead.conversion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The default converter for Map classes.
 */
public final class MapFormConverter {
    private MapFormConverter() {
    }

    /**
     * Encodes the given value in format [key]=[URL encoded value] separated by &
     * @param value the values to encode
     * @param <K> type of the key
     * @param <V> type of the value
     * @return the {@link InputStream} with encoded data
     */
    public static <K, V> InputStream formEncode(Map<K, V> value) {
        var stringValue = value.entrySet()
            .stream()
            .map(entry -> entry.getKey() + "=" + URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
        return new ByteArrayInputStream(stringValue.getBytes(StandardCharsets.UTF_8));
    }
}
