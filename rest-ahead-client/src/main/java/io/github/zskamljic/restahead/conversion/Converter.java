package io.github.zskamljic.restahead.conversion;

import io.github.zskamljic.restahead.client.responses.Response;

import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Serialize the object to input stream.
     *
     * @param object the object to serialize
     * @return the {@link InputStream} containing the serialized object
     * @throws IOException if an exception occurred while serializing
     */
    InputStream serialize(Object object) throws IOException;
}
