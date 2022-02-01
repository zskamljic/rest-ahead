package io.github.zskamljic.restahead.spring;

import io.github.zskamljic.restahead.RestAhead;
import io.github.zskamljic.restahead.conversion.Converter;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
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
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

/**
 * Used to find services and instantiate them as beans.
 */
public class RestAheadRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {
    private static final String BASE_URL = "url";
    private static final String CONVERTER = "converter";

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
                () -> instantiateService(attributes, clazz)
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
     * @param attributes the attributes from which to get the data used during creation
     * @param clazz      the service to instantiate
     * @param <T>        the type to cast to (required since clazz has wildcard type
     * @return the instance of the service
     */
    private <T> T instantiateService(Map<String, Object> attributes, Class<?> clazz) {
        var url = (String) attributes.get(BASE_URL);
        var converter = createConverterIfPossible(attributes);
        var builder = RestAhead.builder(url);
        converter.ifPresent(builder::converter);

        //noinspection unchecked TODO: check if this can be cleanly cast to prevent linter warning
        return (T) builder.build(clazz);
    }

    /**
     * Find a converter from attributes, or return empty optional if none was provided.
     *
     * @param attributes the attributes from which to extract the converter
     * @return converter if a valid class was provided, empty if value was the default converter instance
     * @throws IllegalArgumentException if converter is not a valid subclass or if no valid constructors are present.
     */
    private Optional<Converter> createConverterIfPossible(Map<String, Object> attributes) {
        var value = attributes.get(CONVERTER);
        if (!(value instanceof Class<?> candidateClass)) {
            throw new IllegalArgumentException("Converter must be a subclass of " + Converter.class.getCanonicalName());
        }
        var converterClass = candidateClass.asSubclass(Converter.class);
        if (Converter.class.equals(converterClass)) {
            return Optional.empty();
        }
        try {
            var constructor = converterClass.getConstructor();
            return Optional.of(constructor.newInstance());
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Converter must have a public no-argument constructor.");
        } catch (InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException("An error occurred while instantiating converter", e);
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
