package io.github.zskamljic.restahead.encoding.generation;

import com.squareup.javapoet.MethodSpec;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Used to generate string concatenation
 */
public class GetterFormGenerator {
    private final MethodSpec.Builder builder;

    public GetterFormGenerator(MethodSpec.Builder builder) {
        this.builder = builder;
    }

    /**
     * Generate string concatenation pattern.
     *
     * @param properties       the getters to generate form for
     * @param fieldToGetter mapper for property name to getter name
     */
    public void generateConversion(List<String> properties, UnaryOperator<String> fieldToGetter) {
        var outputBuilder = new StringBuilder();
        outputBuilder.append("var stringValue = ");

        var parameters = new ArrayList<>();
        for (int i = 0; i < properties.size(); i++) {
            var getter = properties.get(i);
            parameters.add((i != 0 ? "&" : "") + getter + "=");
            parameters.add(URLEncoder.class);
            parameters.add(String.class);
            parameters.add(fieldToGetter.apply(getter));
            parameters.add(StandardCharsets.class);

            outputBuilder.append("$S + $T.encode($T.valueOf(value.$L()), $T.UTF_8)");
            if (i != properties.size() - 1) {
                outputBuilder.append(" + \n");
            }
        }
        builder.addStatement(outputBuilder.toString(), parameters.toArray());
    }
}
