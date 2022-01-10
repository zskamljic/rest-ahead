package io.github.zskamljic.restahead.modeling;

import io.github.zskamljic.restahead.adapter.DefaultAdapters;
import io.github.zskamljic.restahead.annotations.Adapter;
import io.github.zskamljic.restahead.modeling.declaration.AdapterClassDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.AdapterMethodDeclaration;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Used to collect all declared adapters.
 */
public class AdapterModeler {
    private final Messager messager;
    private final Elements elements;
    private final Types types;
    private final TypeMirror futureType;

    public AdapterModeler(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.elements = elements;
        this.types = types;
        futureType = elements.getTypeElement(Future.class.getCanonicalName())
            .asType();
    }

    /**
     * Find all adapters, including the default adapters.
     *
     * @param roundEnv the environment from which the adapters can be fetched
     * @return full list of adapter declarations
     */
    public List<AdapterClassDeclaration> findAdapters(RoundEnvironment roundEnv) {
        var defaultAdapters = elements.getTypeElement(DefaultAdapters.class.getCanonicalName());
        var allAdapters = new ArrayList<AdapterClassDeclaration>();
        allAdapters.add(createDefaultAdapterDeclaration(defaultAdapters));
        allAdapters.addAll(findDeclaredAdapters(roundEnv));

        return allAdapters;
    }

    /**
     * Finds adapters annotated by {@link Adapter}.
     *
     * @param roundEnv the environment from which to fetch adapters.
     * @return the list of adapters
     */
    private List<AdapterClassDeclaration> findDeclaredAdapters(RoundEnvironment roundEnv) {
        var declaredAdapters = roundEnv.getElementsAnnotatedWith(Adapter.class);
        var adapters = new HashMap<TypeElement, List<ExecutableElement>>();

        for (var adapter : declaredAdapters) {
            if (!(adapter instanceof ExecutableElement executableElement)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Annotation should only be present on methods.", adapter);
                continue;
            }

            var declaringType = executableElement.getEnclosingElement();
            if (!(declaringType instanceof TypeElement typeElement)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Method should be declared in a class", executableElement);
                continue;
            }

            adapters.merge(typeElement, new ArrayList<>(List.of(executableElement)), (a, b) -> {
                a.addAll(b);
                return a;
            });
        }
        return adapters.entrySet()
            .stream()
            .map(this::createAdapterDeclaration)
            .toList();
    }

    /**
     * Creates a declaration for the adapter type.
     *
     * @param defaultAdapters the adapter from which to generate adapter class
     * @return the adapter class
     */
    private AdapterClassDeclaration createDefaultAdapterDeclaration(TypeElement defaultAdapters) {
        var functions = defaultAdapters.getEnclosedElements()
            .stream()
            .filter(ExecutableElement.class::isInstance)
            .map(ExecutableElement.class::cast)
            .filter(executableElement -> executableElement.getModifiers().contains(Modifier.PUBLIC))
            .map(this::createAdapterMethod)
            .flatMap(Optional::stream)
            .toList();

        return new AdapterClassDeclaration(defaultAdapters, functions);
    }

    /**
     * Attempts to use given method as an adapter.
     *
     * @param executableElement the method to process
     * @return adapter mapping data or empty if errors occurred
     */
    private Optional<AdapterMethodDeclaration> createAdapterMethod(ExecutableElement executableElement) {
        var returnType = executableElement.getReturnType();
        var parameters = executableElement.getParameters();
        if (parameters.isEmpty()) return Optional.empty();

        var adapterParameters = new ArrayList<DeclaredType>();
        for (var parameter : parameters) {
            var type = parameter.asType();
            if (!(type instanceof DeclaredType declaredType) || !types.isAssignable(types.erasure(type), futureType)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Type " + parameter.asType() + " is not supported here", parameter);
                return Optional.empty();
            }
            var genericParameter = declaredType.getTypeArguments().get(0);
            if (!(genericParameter instanceof TypeVariable)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Type specific adapters are not yet supported.", parameter);
                return Optional.empty();
            }
            adapterParameters.add(declaredType);
        }
        return Optional.of(new AdapterMethodDeclaration(
            executableElement.getSimpleName().toString(),
            returnType,
            adapterParameters,
            executableElement.getThrownTypes()
                .stream()
                .map(TypeMirror.class::cast)
                .toList(),
            executableElement
        ));
    }

    /**
     * Collects the map entry to an adapter declaration, with all methods attached to it.
     *
     * @param entry the element type and methods
     * @return the declared class
     */
    private AdapterClassDeclaration createAdapterDeclaration(Map.Entry<TypeElement, List<ExecutableElement>> entry) {
        var type = entry.getKey();
        var value = entry.getValue()
            .stream()
            .map(this::createAdapterMethod)
            .flatMap(Optional::stream)
            .toList();

        return new AdapterClassDeclaration(type, value);
    }
}
