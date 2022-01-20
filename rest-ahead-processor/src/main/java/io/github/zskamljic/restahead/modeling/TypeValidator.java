package io.github.zskamljic.restahead.modeling;

import io.github.zskamljic.restahead.client.requests.parts.FilePart;
import io.github.zskamljic.restahead.client.requests.parts.MultiPart;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Used to validate the type and ensure that a given type is string encodable.
 */
public class TypeValidator {
    private static final List<Class<?>> STRING_REPRESENTABLE_TYPES = List.of(
        String.class, UUID.class
    );
    private static final List<Class<?>> BODY_TYPES = List.of(
        File.class, Path.class
    );

    private final List<TypeMirror> allowedStringMirrors;
    private final List<TypeMirror> allowedFileMirrors;
    private final TypeElement filePartType;
    private final TypeMirror multiPartType;
    private final Types types;

    public TypeValidator(Elements elements, Types types) {
        allowedStringMirrors = mirrorsForTypes(elements, STRING_REPRESENTABLE_TYPES);
        allowedFileMirrors = mirrorsForTypes(elements, BODY_TYPES);
        filePartType = elements.getTypeElement(FilePart.class.getCanonicalName());
        multiPartType = elements.getTypeElement(MultiPart.class.getCanonicalName()).asType();
        this.types = types;
    }

    private List<TypeMirror> mirrorsForTypes(Elements elements, List<Class<?>> classes) {
        return classes.stream()
            .map(Class::getCanonicalName)
            .map(elements::getTypeElement)
            .map(TypeElement::asType)
            .toList();
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

        return allowedStringMirrors.stream()
            .noneMatch(type -> types.isSameType(type, typeMirror));
    }

    /**
     * Checks if the type is valid for file bodies.
     *
     * @param type the type to check
     * @return whether or not the type is valid
     */
    public boolean isFileType(TypeMirror type) {
        return allowedFileMirrors.stream().anyMatch(allowed -> types.isAssignable(type, allowed));
    }

    /**
     * Checks if type can be passed directly.
     *
     * @param type the type
     * @return true if type is field part or file part
     */
    public boolean isDirectMultipartType(TypeMirror type) {
        return types.isAssignable(type, multiPartType);
    }

    /**
     * Get possible exceptions for multipart file constructor based on type provided.
     *
     * @param type the type to check for
     * @return the list of exceptions
     */
    public List<TypeMirror> getPossibleException(TypeMirror type) {
        return filePartType.getEnclosedElements()
            .stream()
            .filter(element -> element.getSimpleName().toString().equals("<init>"))
            .filter(ExecutableElement.class::isInstance)
            .map(ExecutableElement.class::cast)
            .filter(function -> functionContainsParameter(function, type))
            .map(ExecutableElement::getThrownTypes)
            .flatMap(List::stream)
            .distinct()
            .map(TypeMirror.class::cast)
            .toList();
    }

    /**
     * If the function has a parameter that type can be assigned to.
     *
     * @param function the function to check
     * @param type     the type to check for
     * @return whether the type can be used as a parameter to this function
     */
    private boolean functionContainsParameter(ExecutableElement function, TypeMirror type) {
        return function.getParameters()
            .stream()
            .map(VariableElement::asType)
            .anyMatch(p -> types.isAssignable(type, p));
    }
}
