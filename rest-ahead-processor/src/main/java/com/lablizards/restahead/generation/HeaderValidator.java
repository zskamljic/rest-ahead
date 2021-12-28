package com.lablizards.restahead.generation;

import com.lablizards.restahead.requests.parameters.HeaderSpec;

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
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Validates the headers and creates corresponding {@link HeaderSpec}
 */
public class HeaderValidator {
    private static final List<Class<?>> ALLOWED_TYPES = List.of(
        String.class, UUID.class
    );

    // Valid HTTP header characters as per RFC2616, page 31
    private static final Predicate<String> HEADER_REGEX = Pattern.compile("[!-'*+\\-./\\dA-Z^_`a-z|~]+")
        .asMatchPredicate()
        .negate();

    private final Messager messager;
    private final Types types;
    private final List<TypeMirror> allowedMirrors;
    private final TypeMirror collectionMirror;

    /**
     * Create a new instance.
     *
     * @param messager     the messager where errors will be reported to
     * @param elementUtils the Elements object used to fetch type info
     * @param types        an instance of the Types utility
     */
    public HeaderValidator(Messager messager, Elements elementUtils, Types types) {
        this.messager = messager;
        this.types = types;
        allowedMirrors = ALLOWED_TYPES.stream()
            .map(Class::getCanonicalName)
            .map(elementUtils::getTypeElement)
            .map(TypeElement::asType)
            .toList();
        collectionMirror = elementUtils.getTypeElement(Collection.class.getCanonicalName()).asType();
    }

    /**
     * Get a header spec if the variable is a valid request parameter
     *
     * @param value     the header name
     * @param parameter the parameter to generate the header from
     * @return Optional.empty in case of errors, HeaderSpec otherwise
     */
    public Optional<HeaderSpec> getHeaderSpec(String value, VariableElement parameter) {
        if (value.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Generating header names from parameter names not yet supported", parameter);
            return Optional.empty();
        }

        if (HEADER_REGEX.test(value)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Header contains illegal characters", parameter);
            return Optional.empty();
        }

        var type = isInvalidType(parameter.asType());
        if (type == HeaderType.INVALID) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Only primitives, String, UUID and their arrays and collections are supported.", parameter);
            return Optional.empty();
        }

        return Optional.of(new HeaderSpec(value, parameter.getSimpleName().toString(), type == HeaderType.ITERABLE));
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
