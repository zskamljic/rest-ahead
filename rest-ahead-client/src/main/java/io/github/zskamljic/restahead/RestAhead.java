package io.github.zskamljic.restahead;

import io.github.zskamljic.restahead.adapter.DefaultAdapters;
import io.github.zskamljic.restahead.client.Client;
import io.github.zskamljic.restahead.client.JavaHttpClient;
import io.github.zskamljic.restahead.conversion.Converter;
import io.github.zskamljic.restahead.exceptions.RestException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * One-stop shop for initialization of generated services.
 */
public final class RestAhead {
    private RestAhead() {

    }

    /**
     * Creates a new builder for generated services. If no client is specified {@link JavaHttpClient} will be used.
     *
     * @param baseUrl the base url of requests
     * @return a new builder instance
     */
    public static Builder builder(String baseUrl) {
        return new Builder(baseUrl);
    }

    public static class Builder {
        private final String baseUrl;
        private final Map<Class<?>, Object> adapters = new HashMap<>();
        private Client client;
        private Converter converter;

        private Builder(String baseUrl) {
            this.baseUrl = baseUrl;
            adapters.put(DefaultAdapters.class, new DefaultAdapters());
        }

        /**
         * Set a client to use for requests.
         *
         * @param client the client to use
         * @return the updated builder
         */
        public Builder client(Client client) {
            this.client = client;
            return this;
        }

        /**
         * Specify the converter to use with services.
         *
         * @param converter the converter to use
         * @return the updated builder
         */
        public Builder converter(Converter converter) {
            this.converter = converter;
            return this;
        }

        /**
         * Add the adapter to use with services.
         *
         * @param adapter the adapter to register
         * @return the updated builder
         */
        public Builder addAdapter(Object adapter) {
            this.adapters.put(adapter.getClass(), adapter);
            return this;
        }

        /**
         * Builds a new instance of specified service.
         *
         * @param service the service to instantiate
         * @param <T>     the type of the service
         * @return an instance of the requested service.
         */
        public <T> T build(Class<T> service) {
            try {
                var packagePrefix = service.getPackageName();
                if (!packagePrefix.isBlank()) {
                    packagePrefix+=".";
                }

                var implementation = Class.forName(packagePrefix + service.getSimpleName() + "$Impl")
                    .asSubclass(service);

                var parameters = getRequiredParameters(implementation);
                if (parameters[0] != String.class) {
                    throw new RestException("First parameter is not a String, generated code was not correct.");
                }

                var allParameters = new HashMap<>(adapters);
                allParameters.put(Converter.class, converter);
                allParameters.put(Client.class, Optional.ofNullable(client).orElseGet(JavaHttpClient::new));

                var parameterValues = new Object[parameters.length];
                parameterValues[0] = baseUrl;
                for (int i = 1; i < parameterValues.length; i++) {
                    var value = allParameters.get(parameters[i]);
                    if (value == null) {
                        throw new IllegalStateException("No value provided for required parameter " + parameters[i]);
                    }
                    parameterValues[i] = value;
                }
                return implementation.getDeclaredConstructor(parameters)
                    .newInstance(parameterValues);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                throw new RestException(e);
            }
        }

        /**
         * Get the required parameters for the given class constructor.
         *
         * @param implementation the class for which the parameters should be found
         * @param <T>            the type of class
         * @return the constructor parameters
         * @throws IllegalArgumentException if there's more than one constructor
         */
        private <T> Class<?>[] getRequiredParameters(Class<? extends T> implementation) {
            var constructors = implementation.getDeclaredConstructors();
            if (constructors.length != 1) {
                throw new IllegalArgumentException("Illegal class. Only one constructor should be present");
            }
            var constructor = constructors[0];
            return constructor.getParameterTypes();
        }
    }
}
