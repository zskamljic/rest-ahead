package io.github.zskamljic.restahead.modeling.validation;

import io.github.zskamljic.restahead.modeling.TypeValidator;
import io.github.zskamljic.restahead.modeling.declaration.RequestParameterSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.Optional;

/**
 * Used to validate common parts such as headers and queries.
 */
abstract class CommonParameterValidator {
    protected final Messager messager;
    private final Types types;
    private final TypeMirror collectionMirror;
    private final TypeValidator typeValidator;

    protected CommonParameterValidator(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.types = types;
        collectionMirror = elements.getTypeElement(Collection.class.getCanonicalName()).asType();
        typeValidator = new TypeValidator(elements, types);
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
        if (type == ParameterType.INVALID) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Only primitives, String, UUID and their arrays and collections are supported.", parameter);
            return Optional.empty();
        }

        return Optional.of(new RequestParameterSpec(value, parameter.getSimpleName().toString(), type == ParameterType.ITERABLE));
    }

    /**
     * Checks if the type is invalid, single or iterable.
     *
     * @param typeMirror the type of the parameter
     * @return INVALID if type is not supported, SINGLE if item is a directly supported value, ITERABLE if it's an array or {@link Collection}
     */
    private ParameterType isInvalidType(TypeMirror typeMirror) {
        if (typeMirror instanceof ArrayType arrayType) {
            return typeValidator.isUnsupportedType(arrayType.getComponentType()) ? ParameterType.INVALID : ParameterType.ITERABLE;
        }
        if (typeMirror instanceof DeclaredType declaredType) {
            var erasedType = types.erasure(declaredType);
            if (types.isAssignable(erasedType, collectionMirror)) {
                var typeArguments = declaredType.getTypeArguments();
                if (typeArguments.size() != 1) return ParameterType.INVALID;

                return typeValidator.isUnsupportedType(typeArguments.get(0)) ? ParameterType.INVALID : ParameterType.ITERABLE;
            }
        }

        return typeValidator.isUnsupportedType(typeMirror) ? ParameterType.INVALID : ParameterType.SINGLE;
    }

    enum ParameterType {
        INVALID, SINGLE, ITERABLE
    }
}
