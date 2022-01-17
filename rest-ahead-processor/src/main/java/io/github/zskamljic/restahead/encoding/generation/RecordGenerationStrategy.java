package io.github.zskamljic.restahead.encoding.generation;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import io.github.zskamljic.restahead.generation.Variables;
import io.github.zskamljic.restahead.modeling.TypeValidator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Generates a converter for the given record.
 */
public record RecordGenerationStrategy(TypeMirror type) implements GenerationStrategy {
    /**
     * The generated value, using component names as keys and their values as values.
     *
     * @return the generated method
     */
    @Override
    public MethodSpec generateMethod() {
        var builder = MethodSpec.methodBuilder(Variables.FORM_ENCODE)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(TypeName.get(type), "value")
            .returns(InputStream.class);

        var element = ((DeclaredType) type).asElement();
        var getters = accessors(element)
            .map(ExecutableElement::getSimpleName)
            .map(Name::toString)
            .toList();
        var parameters = new ArrayList<>();
        var conversions = new ArrayList<String>();
        for (var getter : getters) {
            parameters.add(getter + "=");
            parameters.add(URLEncoder.class);
            parameters.add(String.class);
            parameters.add(getter);
            parameters.add(StandardCharsets.class);

            conversions.add("$S + $T.encode($T.valueOf(value.$L()), $T.UTF_8)");
        }
        return builder.addStatement("var stringValue = " + String.join(" + \"&\" + ", conversions), parameters.toArray())
            .addStatement("return new $T(stringValue.getBytes())", ByteArrayInputStream.class)
            .build();
    }

    /**
     * Checks if this type is a record that is composed only of string representable types.
     *
     * @param elements the elements to fetch type information from
     * @param types    the types utility to use for typing info
     * @param mirror   the type for which to find a strategy
     * @return the strategy if it can be applied, empty otherwise
     */
    public static Optional<GenerationStrategy> getIfSupported(Elements elements, Types types, TypeMirror mirror) {
        if (!(mirror instanceof DeclaredType declaredType)) return Optional.empty();

        var element = declaredType.asElement();
        if (element.getKind() != ElementKind.RECORD) return Optional.empty();

        var stringValidator = new TypeValidator(elements, types);
        var hasInvalidTypes = accessors(element)
            .map(ExecutableElement::getReturnType)
            .anyMatch(stringValidator::isUnsupportedType);

        if (hasInvalidTypes) return Optional.empty();

        return Optional.of(new RecordGenerationStrategy(mirror));
    }

    /**
     * Create a stream of all component accessors.
     *
     * @param element the element from which to fetch the accessors
     * @return a stream of components
     */
    private static Stream<ExecutableElement> accessors(Element element) {
        return element.getEnclosedElements()
            .stream()
            .filter(RecordComponentElement.class::isInstance)
            .map(RecordComponentElement.class::cast)
            .map(RecordComponentElement::getAccessor);
    }
}
