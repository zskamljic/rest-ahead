package io.github.zskamljic.restahead.spring;

import io.github.zskamljic.restahead.client.Client;
import io.github.zskamljic.restahead.conversion.Converter;
import io.github.zskamljic.restahead.intercepting.Interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for declaring that annotated interface is a RestAhead compatible service. The service will be made into
 * an injectable bean.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RestAheadService {
    /**
     * @return the base url of the service when it's instantiated
     */
    String url();

    /**
     * Converter will be automatically instantiated. The class specified needs to be a bean or has to have
     * a public no-args constructor.
     *
     * @return the Converter class to use with this service. Can be left empty if no converter is required.
     */
    Class<? extends Converter> converter() default Converter.class;

    /**
     * Client will be automatically instantiated. The class specified needs to be a bean or has to have
     * a public no-args constructor.
     *
     * @return the Client to use with this service. Can be left empty for default client.
     */
    Class<? extends Client> client() default Client.class;

    /**
     * Interceptors will be automatically instantiated. The class specified needs to be a bean or has to have
     * a public no-args constructor.
     * This will only be honored if a client is provided.
     *
     * @return the Interceptor classes to use with the client.
     */
    Class<? extends Interceptor>[] interceptors() default {};

    /**
     * Adapters will be automatically instantiated. The class specified needs to be a bean or has to have
     * a public no-args constructor.
     *
     * @return the Adapter classes to use with this service. Can be left empty if no adapters are required.
     */
    Class<?>[] adapters() default {};
}
