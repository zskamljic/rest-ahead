package com.lablizards.restahead;

import com.lablizards.restahead.client.JavaHttpClient;
import com.lablizards.restahead.client.RestClient;
import com.lablizards.restahead.conversion.Converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
        private RestClient client;
        private Converter converter;

        private Builder(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        /**
         * Set a client to use for requests.
         *
         * @param client the client to use
         * @return the updated builder
         */
        public Builder client(RestClient client) {
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
         * Builds a new instance of specified service.
         *
         * @param service the service to instantiate
         * @param <T>     the type of the service
         * @return an instance of the requested service.
         */
        public <T> T build(Class<T> service) {
            try {
                var implementation = Class.forName(service.getName() + "$Impl")
                    .asSubclass(service);
                if (client == null) {
                    client = new JavaHttpClient(baseUrl);
                }

                var clientOnlyConstructor = getClientOnlyConstructor(implementation);
                if (clientOnlyConstructor.isPresent()) {
                    return clientOnlyConstructor.get().newInstance(client);
                }
                var converterConstructor = getConverterConstructor(implementation);
                if (converterConstructor.isPresent()) {
                    if (converter == null) {
                        throw new IllegalStateException("Converter required to instantiate " + service + ", but none was provided");
                    }
                    return converterConstructor.get().newInstance(client, converter);
                }
                throw new RuntimeException("Unable to construct instance of " + service + ", no valid constructors were present");
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Get constructor with client parameter, swallow the exception, as we only care if it exists or not.
         *
         * @param implementation the class from which to get the constructor
         * @param <T>            the constructed type
         * @return empty if constructor does not exist or constructor otherwise
         */
        private <T> Optional<Constructor<? extends T>> getClientOnlyConstructor(Class<? extends T> implementation) {
            try {
                return Optional.of(implementation.getDeclaredConstructor(RestClient.class));
            } catch (NoSuchMethodException e) {
                return Optional.empty();
            }
        }

        /**
         * Get constructor with client, swallowing exceptions if it does not exist
         *
         * @param implementation the class from which to fetch the constructor
         * @param <T>            the constructed type
         * @return empty if constructor doesn't exist, the constructor otherwise
         */
        private <T> Optional<Constructor<? extends T>> getConverterConstructor(Class<? extends T> implementation) {
            try {
                return Optional.of(implementation.getDeclaredConstructor(RestClient.class, Converter.class));
            } catch (NoSuchMethodException e) {
                return Optional.empty();
            }
        }
    }
}
