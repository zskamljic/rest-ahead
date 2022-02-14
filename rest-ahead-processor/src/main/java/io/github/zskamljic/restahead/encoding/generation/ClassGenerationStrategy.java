package io.github.zskamljic.restahead.encoding.generation;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import io.github.zskamljic.restahead.generation.Variables;
import io.github.zskamljic.restahead.modeling.TypeValidator;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Used to generate class to form encoded string conversion.
 */
public record ClassGenerationStrategy(TypeMirror type) implements FormConversionStrategy {
    public MethodSpec generate() {
        var builder = MethodSpec.methodBuilder(Variables.FORM_ENCODE)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(TypeName.get(type), "value")
            .returns(InputStream.class);

        var getters = findGetters((DeclaredType) type);

        var getterFormGenerator = new GetterFormGenerator(builder);
        getterFormGenerator.generateConversion(getters, ClassGenerationStrategy::getterToName);
        return builder.addStatement("return new $T(stringValue.getBytes())", ByteArrayInputStream.class)
            .build();
    }

    /**
     * Checks if type is a class and contains only valid return types on getters.
     *
     * @param elements the elements to fetch type information from
     * @param types    the types utility to use for typing info
     * @param mirror   the type for which to find a strategy
     * @return generation strategy if no issues were discovered, empty otherwise
     */
    public static Optional<FormConversionStrategy> getIfSupported(Messager messager, Elements elements, Types types, TypeMirror mirror) {
        if (!(mirror instanceof DeclaredType declaredType)) return Optional.empty();

        var getters = findGetters(declaredType);

        if (getters.isEmpty()) return Optional.empty();

        var stringValidator = new TypeValidator(elements, types);
        var hasInvalidType = getters.stream()
            .map(ExecutableElement::getReturnType)
            .anyMatch(stringValidator::isUnsupportedType);

        if (hasInvalidType || GetterFormGenerator.hasInvalidFormNames(messager, false, getters.stream())) {
            return Optional.empty();
        }

        return Optional.of(new ClassGenerationStrategy(mirror));
    }

    /**
     * Finds all getters in provided class.
     *
     * @param declaredType the type to find the getters for
     * @return list of public getters
     */
    private static List<ExecutableElement> findGetters(DeclaredType declaredType) {
        return declaredType.asElement()
            .getEnclosedElements()
            .stream()
            .filter(ExecutableElement.class::isInstance)
            .map(ExecutableElement.class::cast)
            .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
            .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
            .filter(e -> e.getParameters().isEmpty())
            .filter(e -> e.getSimpleName().toString().matches("get[A-Z].*"))
            .toList();
    }

    /**
     * Strip the prefix from getter and lowercase first letter.
     *
     * @param getter the getter to rename
     * @return the renamed getter
     */
    private static String getterToName(String getter) {
        var name = getter.replaceFirst("^get", "");
        return name.substring(0, 1).toLowerCase() +
            name.substring(1);
    }
}
