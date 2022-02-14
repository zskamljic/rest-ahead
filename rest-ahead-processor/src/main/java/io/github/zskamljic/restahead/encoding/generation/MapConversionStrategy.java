package io.github.zskamljic.restahead.encoding.generation;

import io.github.zskamljic.restahead.modeling.TypeValidator;

import javax.annotation.processing.Messager;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Map;
import java.util.Optional;

/**
 * Generates a form converter for maps.
 */
public record MapConversionStrategy(TypeMirror type) implements FormConversionStrategy {

    /**
     * Checks if provided type is a Map or one of the subclasses that has string representable keys and values.
     *
     * @param elements the elements to fetch type information from
     * @param types    the types utility to use for typing info
     * @param mirror   the type for which to find a strategy
     * @return a strategy if data is valid, empty otherwise
     */
    public static Optional<FormConversionStrategy> getIfSupported(Messager messager, Elements elements, Types types, TypeMirror mirror) {
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
            messager.printMessage(Diagnostic.Kind.ERROR, "Maps must consist of string representable values to be formEncoded", mapType.asElement());
            return Optional.empty();
        }

        return Optional.of(new MapConversionStrategy(types.erasure(type)));
    }
}
