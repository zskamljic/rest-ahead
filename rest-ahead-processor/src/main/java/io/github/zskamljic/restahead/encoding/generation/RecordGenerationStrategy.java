package io.github.zskamljic.restahead.encoding.generation;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import io.github.zskamljic.restahead.generation.Variables;
import io.github.zskamljic.restahead.modeling.TypeValidator;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Generates a converter for the given record.
 */
public record RecordGenerationStrategy(TypeMirror type) implements FormConversionStrategy {

    public MethodSpec generate() {
        var builder = MethodSpec.methodBuilder(Variables.FORM_ENCODE)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(TypeName.get(type), "value")
            .returns(InputStream.class);

        var element = ((DeclaredType) type).asElement();
        var getterFormGenerator = new GetterFormGenerator(builder);
        getterFormGenerator.generateConversion(accessors(element).toList(), UnaryOperator.identity());
        return builder.addStatement("return new $T(stringValue.getBytes())", ByteArrayInputStream.class)
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
    public static Optional<FormConversionStrategy> getIfSupported(Messager messager, Elements elements, Types types, TypeMirror mirror) {
        if (!(mirror instanceof DeclaredType declaredType)) return Optional.empty();

        var element = declaredType.asElement();
        if (element.getKind() != ElementKind.RECORD) return Optional.empty();

        var stringValidator = new TypeValidator(elements, types);
        var hasInvalidTypes = accessors(element)
            .map(ExecutableElement::getReturnType)
            .anyMatch(stringValidator::isUnsupportedType);

        if (hasInvalidTypes || GetterFormGenerator.hasInvalidFormNames(messager, true, accessors(element))) {
            return Optional.empty();
        }

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
