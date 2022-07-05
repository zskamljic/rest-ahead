package io.github.zskamljic.restahead.conversion;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.client.responses.Response;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OptionsConverter {
    public static List<Verb> parseOptions(Response response) {
        var values = response.headers().get("Allow");
        return values.stream()
            .map(allow -> allow.split(", "))
            .flatMap(Arrays::stream)
            .map(String::trim)
            .mapMulti((BiConsumer<String, Consumer<Verb>>) (value, consumer) -> {
                if ("*".equals(value)) {
                    Arrays.stream(Verb.values()).forEach(consumer);
                }
                consumer.accept(Verb.valueOf(value));
            })
            .distinct()
            .toList();
    }
}
