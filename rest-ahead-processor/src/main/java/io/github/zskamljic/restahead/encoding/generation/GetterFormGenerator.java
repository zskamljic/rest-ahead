package io.github.zskamljic.restahead.encoding.generation;

import com.squareup.javapoet.MethodSpec;
import io.github.zskamljic.restahead.annotations.form.FormName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

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
     * @param properties        the property names mapped to their getters
     * @param defaultNameMapper the default mapping from function name to property name
     */
    public void generateConversion(Collection<ExecutableElement> properties, UnaryOperator<String> defaultNameMapper) {
        var outputBuilder = new StringBuilder();
        outputBuilder.append("var stringValue = ");

        var parameters = new ArrayList<>();
        var iterator = properties.iterator();
        var addedProperty = false;
        while (iterator.hasNext()) {
            var accessor = iterator.next();
            var property = accessorToName(accessor, defaultNameMapper);
            var getter = accessor.getSimpleName().toString();
            parameters.add((addedProperty ? "&" : "") + property + "=");
            parameters.add(URLEncoder.class);
            parameters.add(String.class);
            parameters.add(getter);
            parameters.add(StandardCharsets.class);

            outputBuilder.append("$S + $T.encode($T.valueOf(value.$L()), $T.UTF_8)");
            if (iterator.hasNext()) {
                outputBuilder.append(" + \n");
            }
            addedProperty = true;
        }
        builder.addStatement(outputBuilder.toString(), parameters.toArray());
    }

    /**
     * Maps the accessor function to the name of the form parameter.
     *
     * @param executableElement the function to get the name from
     * @param defaultNameMapper the mapper for function name used to modify the name if needed
     * @return the property name
     */
    private String accessorToName(ExecutableElement executableElement, UnaryOperator<String> defaultNameMapper) {
        return Optional.ofNullable(executableElement.getAnnotation(FormName.class))
            .map(FormName::value)
            .orElseGet(() -> defaultNameMapper.apply(executableElement.getSimpleName().toString()));
    }

    /**
     * Checks if there's any invalid form names specified on the getters that will be used.
     *
     * @param messager  the messager to report errors to
     * @param isRecord  if enclosing element is a record
     * @param accessors the accessors to check for names
     * @return true if any errors are present, false otherwise
     */
    public static boolean hasInvalidFormNames(Messager messager, boolean isRecord, Stream<ExecutableElement> accessors) {
        var emptyFormNames = accessors.filter(function -> {
                var annotation = function.getAnnotation(FormName.class);
                return annotation != null && annotation.value().isBlank();
            })
            .toList();

        for (var function : emptyFormNames) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Form name cannot be empty.",
                isRecord ? function.getEnclosingElement() : function
            );
        }
        return !emptyFormNames.isEmpty();
    }
}
