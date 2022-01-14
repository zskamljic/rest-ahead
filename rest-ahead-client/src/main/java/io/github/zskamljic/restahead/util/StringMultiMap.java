package io.github.zskamljic.restahead.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Common collection to use for map of string to list of strings (i.e. headers, query items).
 */
public class StringMultiMap extends HashMap<String, List<String>> {
    /**
     * Creates a deep copy of this instance.
     *
     * @return the copied value
     */
    public StringMultiMap mutableCopy() {
        var copiedMap = new StringMultiMap();
        forEach((key, value) -> copiedMap.put(key, List.copyOf(value)));
        return copiedMap;
    }

    /**
     * Creates a new immutable copy of this instance.
     *
     * @return the immutable copy
     */
    public Map<String, List<String>> immutableCopy() {
        return entrySet()
            .stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> Collections.unmodifiableList(entry.getValue())
            ));
    }
}
