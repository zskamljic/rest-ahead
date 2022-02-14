package io.github.zskamljic.restahead.spring;

import io.github.zskamljic.restahead.RestAhead;
import io.github.zskamljic.restahead.client.Client;
import io.github.zskamljic.restahead.conversion.Converter;
import io.github.zskamljic.restahead.intercepting.Interceptor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Used to find services and instantiate them as beans.
 */
public class RestAheadRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {
    private static final String BASE_URL = "url";
    private static final String CONVERTER = "converter";
    private static final String CLIENT = "client";
    private static final String INTERCEPTORS = "interceptors";
    private static final String ADAPTERS = "adapters";

    private Environment environment;
    private ResourceLoader resourceLoader;

    /**
     * Register beans specific to RestAhead.
     *
     * @param metadata the metadata of enable annotation
     * @param registry the registry of all beans
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerRestAheadServices(metadata, registry);
    }

    /**
     * Find services annotated with {@link RestAheadService} and generates bean definitions for them.
     *
     * @param metadata the metadata of enable annotation
     * @param registry the registry of all beans
     */
    private void registerRestAheadServices(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        var scanner = getScanner();
        scanner.setResourceLoader(resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestAheadService.class));
        var components = scanner.findCandidateComponents(ClassUtils.getPackageName(metadata.getClassName()));

        components.stream()
            .filter(AnnotatedBeanDefinition.class::isInstance)
            .map(AnnotatedBeanDefinition.class::cast)
            .forEach(definition -> registerService(definition, registry));
    }

    /**
     * Registers the found service.
     *
     * @param definition the definition for given bean
     * @param registry   the registry to which to add the new bean
     */
    private void registerService(AnnotatedBeanDefinition definition, BeanDefinitionRegistry registry) {
        var metadata = definition.getMetadata();
        var className = metadata.getClassName();
        var attributes = metadata.getAnnotationAttributes(RestAheadService.class.getName());
        if (CollectionUtils.isEmpty(attributes)) return;

        var clazz = ClassUtils.resolveClassName(className, null);

        var definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz,
                () -> instantiateService(registry, attributes, clazz)
            )
            .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
            .setLazyInit(true);
        AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();

        var holder = new BeanDefinitionHolder(beanDefinition, className);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * Instantiate a new service with appropriate url, converter etc.
     *
     * @param <T>        the type to cast to (required since clazz has wildcard type
     * @param registry   the registry to which to add the new bean
     * @param attributes the attributes from which to get the data used during creation
     * @param clazz      the service to instantiate
     * @return the instance of the service
     */
    private <T> T instantiateService(BeanDefinitionRegistry registry, Map<String, Object> attributes, Class<?> clazz) {
        var url = environment.resolvePlaceholders((String) attributes.get(BASE_URL));
        var beanFactory = registry instanceof ConfigurableBeanFactory factory ? factory : null;
        var converter = getConverterIfPossible(beanFactory, attributes);
        var client = getClientIfPossible(beanFactory, attributes);
        var adapters = getAdaptersIfPossible(beanFactory, attributes);
        var builder = RestAhead.builder(url);
        converter.ifPresent(builder::converter);
        client.ifPresent(builder::client);
        adapters.forEach(builder::addAdapter);

        //noinspection unchecked
        return (T) builder.build(clazz);
    }

    /**
     * Find a converter from attributes, or return empty optional if none was provided.
     *
     * @param beanFactory the bean factory to fetch bean instances from
     * @param attributes  the attributes from which to extract the converter
     * @return converter if a valid class was provided, empty if value was the default converter instance
     * @throws IllegalArgumentException if converter is not a valid subclass or if no valid constructors are present.
     */
    private Optional<Converter> getConverterIfPossible(
        @Nullable ConfigurableBeanFactory beanFactory,
        Map<String, Object> attributes
    ) {
        var value = attributes.get(CONVERTER);
        return getInstanceForClass(beanFactory, value, CONVERTER, Converter.class);
    }

    /**
     * Get instance of client if specified. If interceptors are found they are added to client instance as well.
     *
     * @param beanFactory the bean factory to fetch bean instances from
     * @param attributes  the full set of attributes
     * @return client if one could be found from config, empty otherwise
     */
    private Optional<Client> getClientIfPossible(
        @Nullable ConfigurableBeanFactory beanFactory,
        Map<String, Object> attributes
    ) {
        var value = attributes.get(CLIENT);
        var client = getInstanceForClass(beanFactory, value, CLIENT, Client.class);
        if (client.isEmpty()) return Optional.empty();

        var interceptorValues = attributes.get(INTERCEPTORS);
        if (!(interceptorValues instanceof Class<?>[] interceptorClasses)) {
            throw new IllegalStateException(INTERCEPTORS + " must be an array of classes.");
        }
        var interceptors = Arrays.stream(interceptorClasses)
            .flatMap(clazz -> getInstanceForClass(beanFactory, clazz, INTERCEPTORS, Interceptor.class).stream())
            .toList();
        var clientInstance = client.get();
        interceptors.forEach(clientInstance::addInterceptor);
        return Optional.of(clientInstance);
    }

    /**
     * Find and instantiate adapters from specified classes.
     *
     * @param beanFactory the bean factory to fetch bean instances from
     * @param attributes  the attributes from which to get the adapters
     * @return list of adapter instances
     */
    private List<Object> getAdaptersIfPossible(
        @Nullable ConfigurableBeanFactory beanFactory,
        Map<String, Object> attributes
    ) {
        var values = attributes.get(ADAPTERS);
        if (!(values instanceof Class<?>[] classes)) {
            throw new IllegalStateException(ADAPTERS + " must be an array of classes.");
        }
        return Arrays.stream(classes)
            .flatMap(clazz -> getInstanceForClass(beanFactory, clazz, ADAPTERS, Object.class).stream())
            .toList();
    }

    /**
     * Create a new instance of provided class and cast accordingly.
     *
     * @param beanFactory the bean factory to fetch existing bean from if possible
     * @param value       the attribute value
     * @param name        the name of the attribute
     * @param targetClass class that needs to be instantiated
     * @param <T>         the type to return
     * @return instance of type if possible, empty if class is not the default value
     */
    private <T> Optional<T> getInstanceForClass(
        @Nullable ConfigurableBeanFactory beanFactory,
        Object value,
        String name,
        Class<T> targetClass
    ) {
        if (!(value instanceof Class<?> candidateClass)) {
            throw new IllegalStateException(name + " must be a subclass of " + targetClass.getCanonicalName());
        }
        var selectedClass = candidateClass.asSubclass(targetClass);
        var beanInstance = findBeanInstance(beanFactory, selectedClass);
        if (beanInstance.isPresent()) {
            return beanInstance.map(selectedClass::cast);
        }
        if (targetClass.equals(candidateClass)) {
            return Optional.empty();
        }
        try {
            var constructor = selectedClass.getConstructor();
            return Optional.of(constructor.newInstance());
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalArgumentException(name + " must have a public no-argument constructor.");
        } catch (InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException("An error occurred while instantiating " + name, e);
        }
    }

    private <T> Optional<T> findBeanInstance(
        @Nullable ConfigurableBeanFactory beanFactory,
        Class<? extends T> selectedClass
    ) {
        if (beanFactory == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(beanFactory.getBean(selectedClass));
        } catch (NoSuchBeanDefinitionException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Create a scanner that only finds independent interfaces from environment.
     *
     * @return the scanner configured for service discovery
     */
    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                var metadata = beanDefinition.getMetadata();
                return metadata.isIndependent() && metadata.isInterface();
            }
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
