package io.github.zskamljic.restahead.encoding.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import io.github.zskamljic.restahead.generation.Variables;
import io.github.zskamljic.restahead.modeling.TypeValidator;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Generates a form converter for maps.
 */
public record MapGenerationStrategy(TypeMirror type) implements GenerationStrategy {

    /**
     * Generates the map converter, mapping keys to value.
     *
     * @return the generated method
     */
    @Override
    public MethodSpec generateMethod() {
        var keyParameter = TypeVariableName.get("Key");
        var valueParameter = TypeVariableName.get("Value");
        var builder = MethodSpec.methodBuilder(Variables.FORM_ENCODE)
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .addParameter(ParameterizedTypeName.get((ClassName) TypeName.get(type), keyParameter, valueParameter), "value")
            .addTypeVariables(List.of(keyParameter, valueParameter))
            .returns(InputStream.class);

        return builder.addStatement(
                """
                    var stringValue = value.entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + "=" + $T.encode($T.valueOf(entry.getValue()), $T.UTF_8))
                        .collect($T.joining("&"))""",
                URLEncoder.class,
                String.class,
                StandardCharsets.class,
                Collectors.class
            )
            .addStatement("return new $T(stringValue.getBytes())", ByteArrayInputStream.class)
            .build();
    }

    /**
     * Checks if provided type is a Map or one of the subclasses that has string representable keys and values.
     *
     * @param elements the elements to fetch type information from
     * @param types    the types utility to use for typing info
     * @param mirror   the type for which to find a strategy
     * @return a strategy if data is valid, empty otherwise
     */
    public static Optional<GenerationStrategy> getIfSupported(Elements elements, Types types, TypeMirror mirror) {
        var type = elements.getTypeElement(Map.class.getCanonicalName())
            .asType();
        if (!types.isAssignable(types.erasure(mirror), type)) {
            return Optional.empty();
        }
        var mapType = (DeclaredType) mirror;
        var genericArguments = mapType.getTypeArguments();
        if (genericArguments.size() != 2) return Optional.empty();

        var stringValidator = new TypeValidator(elements, types);
        var key = genericArguments.get(0);
        var value = genericArguments.get(1);

        if (stringValidator.isUnsupportedType(key) || stringValidator.isUnsupportedType(value)) {
            return Optional.empty();
        }

        return Optional.of(new MapGenerationStrategy(types.erasure(type)));
    }
}
