package com.lablizards.restahead.modeling.validation;

import com.lablizards.restahead.modeling.declaration.RequestParameterSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Used to validate common parameters such as headers and queries.
 */
abstract class CommonParameterValidator {
    private static final List<Class<?>> ALLOWED_TYPES = List.of(
        String.class, UUID.class
    );

    private final List<TypeMirror> allowedMirrors;
    protected final Messager messager;
    private final Types types;
    private final TypeMirror collectionMirror;

    protected CommonParameterValidator(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.types = types;
        collectionMirror = elements.getTypeElement(Collection.class.getCanonicalName()).asType();

        allowedMirrors = ALLOWED_TYPES.stream()
            .map(Class::getCanonicalName)
            .map(elements::getTypeElement)
            .map(TypeElement::asType)
            .toList();
    }

    /**
     * Extract the parameter spec for given variable
     *
     * @param parameter the parameter to fetch info from
     * @param value     the value used for HTTP name
     * @return empty for invalid setup, non empty for valid config
     */
    protected Optional<RequestParameterSpec> extractSpec(VariableElement parameter, String value) {
        var type = isInvalidType(parameter.asType());
        if (type == HeaderType.INVALID) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Only primitives, String, UUID and their arrays and collections are supported.", parameter);
            return Optional.empty();
        }

        return Optional.of(new RequestParameterSpec(value, parameter.getSimpleName().toString(), type == HeaderType.ITERABLE));
    }

    /**
     * Checks if the type is invalid, single or iterable.
     *
     * @param typeMirror the type of the parameter
     * @return INVALID if type is not supported, SINGLE if item is a directly supported value, ITERABLE if it's an array or {@link Collection}
     */
    private HeaderType isInvalidType(TypeMirror typeMirror) {
        if (typeMirror instanceof ArrayType arrayType) {
            return isUnsupportedType(arrayType.getComponentType()) ? HeaderType.INVALID : HeaderType.ITERABLE;
        }
        if (typeMirror instanceof DeclaredType declaredType) {
            var erasedType = types.erasure(declaredType);
            if (types.isAssignable(erasedType, collectionMirror)) {
                var typeArguments = declaredType.getTypeArguments();
                if (typeArguments.size() != 1) return HeaderType.INVALID;

                return isUnsupportedType(typeArguments.get(0)) ? HeaderType.INVALID : HeaderType.ITERABLE;
            }
        }

        return isUnsupportedType(typeMirror) ? HeaderType.INVALID : HeaderType.SINGLE;
    }

    /**
     * Checks if type is supported.
     *
     * @param typeMirror the type to check
     * @return true if value is not a primitive, a boxed primitive or not a String or UUID
     */
    private boolean isUnsupportedType(TypeMirror typeMirror) {
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

    enum HeaderType {
        INVALID, SINGLE, ITERABLE
    }
}
