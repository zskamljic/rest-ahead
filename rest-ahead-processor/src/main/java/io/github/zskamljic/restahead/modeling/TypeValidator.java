package io.github.zskamljic.restahead.modeling;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.UUID;

/**
 * Used to validate the type and ensure that a given type is string encodable.
 */
public class TypeValidator {
    public static final List<Class<?>> STRING_REPRESENTABLE_TYPES = List.of(
        String.class, UUID.class
    );

    private final List<TypeMirror> allowedMirrors;
    private final Types types;

    public TypeValidator(Elements elements, Types types) {
        allowedMirrors = STRING_REPRESENTABLE_TYPES.stream()
            .map(Class::getCanonicalName)
            .map(elements::getTypeElement)
            .map(TypeElement::asType)
            .toList();
        this.types = types;
    }

    /**
     * Checks if type is supported.
     *
     * @param typeMirror the type to check
     * @return true if value is not a primitive, a boxed primitive or not a String or UUID
     */
    public boolean isUnsupportedType(TypeMirror typeMirror) {
        if (typeMirror.getKind().isPrimitive()) {
            return false;
        }
        try {
            types.unboxedType(typeMirror);
            return false;
        } catch (IllegalArgumentException exception) {
            // Type was not a primitive
        }

        return allowedMirrors.stream()
            .noneMatch(type -> types.isSameType(type, typeMirror));
    }
}
