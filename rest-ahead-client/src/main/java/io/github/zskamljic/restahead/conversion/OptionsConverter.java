package io.github.zskamljic.restahead.conversion;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.client.responses.Response;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class OptionsConverter {
    private OptionsConverter() {
    }

    public static List<Verb> parseOptions(Response response) {
        return response.headers().entrySet()
            .stream()
            .filter(e -> e.getKey().equalsIgnoreCase("allow"))
            .map(Map.Entry::getValue)
            .flatMap(Collection::stream)
            .map(allow -> allow.split(", "))
            .flatMap(Arrays::stream)
            .map(String::trim)
            .mapMulti((BiConsumer<String, Consumer<Verb>>) (value, consumer) -> {
                if ("*".equals(value)) {
                    Arrays.stream(Verb.values()).forEach(consumer);
                    return;
                }
                consumer.accept(Verb.valueOf(value));
            })
            .distinct()
            .toList();
    }
}
