package io.github.zskamljic.restahead;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.zskamljic.restahead.client.Response;
import io.github.zskamljic.restahead.conversion.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Type;

/**
 * Simple Jackson converter to be used with RestAhead.
 */
public class JacksonConverter implements Converter {
    private final ObjectMapper objectMapper;

    /**
     * Construct a new instance, using the default settings for ObjectMapper
     */
    public JacksonConverter() {
        this(new ObjectMapper());
    }

    /**
     * Use the provided object mapper for this converter
     *
     * @param objectMapper the mapper to use
     */
    public JacksonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Deserialize the type from given response.
     *
     * @param response the response to deserialize from
     * @param type     the type to deserialize to
     * @param <T>      the target type
     * @return the deserialized object
     * @throws IOException if an error occurred while deserializing the object
     */
    @Override
    public <T> T deserialize(Response response, Type type) throws IOException {
        var javaType = objectMapper.getTypeFactory().constructType(type);
        var reader = objectMapper.readerFor(javaType);
        return reader.readValue(response.body());
    }

    /**
     * Serializes the provided object to JSON.
     *
     * @param object the object to serialize
     * @return {@link InputStream} containing the serialized object
     * @throws IOException if an exception occurs while mapping to json
     */
    @Override
    public InputStream serialize(Object object) throws IOException {
        var pipedInput = new PipedInputStream();
        try (var output = new PipedOutputStream(pipedInput)) {
            objectMapper.writeValue(output, object);
        }
        return pipedInput;
    }
}
