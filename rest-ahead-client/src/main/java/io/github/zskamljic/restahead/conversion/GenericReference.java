package io.github.zskamljic.restahead.conversion;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Generic reference used for deserialization of generic object such as Collections.
 * Intended only for generated code, instantiating manually voids all warranty.
 *
 * @param <T> the contained type.
 */
@SuppressWarnings("unused") // T is used via reflection, so the compiler believes it is unused
public abstract class GenericReference<T> {
    private final Type type;

    protected GenericReference() {
        var superclass = getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType parameterizedType)) {
            throw new IllegalArgumentException("Class is not a generic type.");
        }

        type = parameterizedType.getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
