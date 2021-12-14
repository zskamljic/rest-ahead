package com.lablizards.restahead;

import com.lablizards.restahead.client.JavaHttpClient;
import com.lablizards.restahead.client.RestClient;

import java.lang.reflect.InvocationTargetException;

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
                var constructor = implementation.getDeclaredConstructor(RestClient.class);
                if (client == null) {
                    client = new JavaHttpClient(baseUrl);
                }
                return constructor.newInstance(client);
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
