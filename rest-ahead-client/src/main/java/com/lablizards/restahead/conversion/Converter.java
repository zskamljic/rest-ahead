package com.lablizards.restahead.conversion;

import com.lablizards.restahead.client.Response;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Used to convert types from their serialized form to Java objects.
 */
public interface Converter {
    /**
     * Deserialize the response to given type
     *
     * @param response the response to deserialize from
     * @param type     the type to deserialize to
     * @param <T>      the return type
     * @return the deserialized value
     */
    <T> T deserialize(Response response, Type type) throws IOException;
}
