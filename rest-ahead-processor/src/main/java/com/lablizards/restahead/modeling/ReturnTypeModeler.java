package com.lablizards.restahead.modeling;

import com.lablizards.restahead.adapter.DefaultAdapters;
import com.lablizards.restahead.client.Response;
import com.lablizards.restahead.modeling.declaration.AdapterClassDeclaration;
import com.lablizards.restahead.modeling.declaration.AdapterMethodDeclaration;
import com.lablizards.restahead.modeling.declaration.ReturnAdapterCall;
import com.lablizards.restahead.modeling.declaration.ReturnDeclaration;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Used for collecting return type converters and adapters.
 */
public class ReturnTypeModeler {
    private final Messager messager;
    private final Types types;
    private final TypeMirror futureType;
    private final TypeMirror defaultAdapterType;
    private final DeclaredType defaultResponseType;
    private final TypeMirror responseType;

    public ReturnTypeModeler(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.types = types;
        var futureElement = elements.getTypeElement(Future.class.getCanonicalName());
        responseType = elements.getTypeElement(Response.class.getCanonicalName())
            .asType();
        futureType = types.erasure(futureElement.asType());
        defaultResponseType = types.getDeclaredType(futureElement, responseType);
        defaultAdapterType = elements.getTypeElement(DefaultAdapters.class.getCanonicalName()).asType();
    }

    /**
     * Returns the return declaration if all data could be extracted.
     *
     * @param function the function from which to obtain the data
     * @param adapters the return type adapters
     * @return empty in case of errors, the declaration otherwise
     */
    public Optional<ReturnDeclaration> getReturnConfiguration(
        ExecutableElement function,
        List<AdapterClassDeclaration> adapters
    ) {
        var returnType = function.getReturnType();

        if (types.isSameType(returnType, defaultResponseType)) {
            return Optional.of(new ReturnDeclaration(Optional.empty(), Optional.empty()));
        } else if (types.isSubtype(types.erasure(returnType), futureType)) {
            var convertedType = ((DeclaredType) returnType).getTypeArguments().get(0);
            return Optional.of(new ReturnDeclaration(Optional.of(convertedType), Optional.empty()));
        } else {
            var selectedAdapter = findCorrectAdapter(adapters, returnType);
            if (selectedAdapter.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR, "No adapter to convert element from " + defaultResponseType + " to " + returnType, function);
                return Optional.empty();
            }

            var adaptedConvertType = findAdaptedConvertType(returnType, selectedAdapter.get());
            ReturnDeclaration returnDeclaration;
            if (types.isSameType(adaptedConvertType, responseType) || adaptedConvertType.getKind() == TypeKind.VOID) {
                returnDeclaration = new ReturnDeclaration(Optional.empty(), selectedAdapter);
            } else {
                returnDeclaration = new ReturnDeclaration(Optional.of(adaptedConvertType), selectedAdapter);
            }
            return Optional.of(returnDeclaration);
        }
    }

    /**
     * Attempts to find the best adapter for the given return type.
     *
     * @param adapters   list of adapters
     * @param returnType the target type
     * @return the adapter call if found or empty
     */
    private Optional<ReturnAdapterCall> findCorrectAdapter(List<AdapterClassDeclaration> adapters, TypeMirror returnType) {
        var validAdapters = new ArrayList<ReturnAdapterCall>();

        for (var adapterClass : adapters) {
            for (var adapter : adapterClass.adapterMethods()) {
                var adapterResultType = adapter.returnType();
                if (adapterResultType instanceof TypeVariable typeVariable) {
                    if (types.isAssignable(returnType, typeVariable.getUpperBound())) {
                        validAdapters.add(new ReturnAdapterCall(adapterClass, adapter));
                    }
                } else if (types.isAssignable(types.erasure(returnType), adapterResultType)) {
                    validAdapters.add(new ReturnAdapterCall(adapterClass, adapter));
                }
            }
        }
        if (validAdapters.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "No adapter found to convert to type " + returnType);
            return Optional.empty();
        }
        if (validAdapters.size() != 1) {
            var nonDefaultAdapters = findNonDefaultAdapters(validAdapters);
            if (nonDefaultAdapters.size() == 1) {
                return Optional.of(nonDefaultAdapters.get(0));
            }

            messager.printMessage(Diagnostic.Kind.ERROR, "More than one adapter exists for target type " +
                returnType + ": " +
                validAdapters.stream()
                    .map(ReturnAdapterCall::adapterMethod)
                    .map(AdapterMethodDeclaration::executableElement)
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")));
            return Optional.empty();
        }

        return Optional.of(validAdapters.get(0));
    }

    /**
     * Finds the type to convert to if adapter is used.
     *
     * @param returnType the return type as declared
     * @param adapter    the adapter being used
     * @return the type to deserialize to
     */
    private TypeMirror findAdaptedConvertType(
        TypeMirror returnType,
        ReturnAdapterCall adapter
    ) {
        var adaptedType = adapter.adapterMethod().returnType();

        // Get the name of generic argument (in case it's not 'T'), if none is present stop
        var inputGenericType = adapter.adapterMethod().adapterParameters()
            .stream()
            .filter(type -> types.isSameType(types.erasure(type), futureType))
            .findFirst();
        if (inputGenericType.isEmpty()) {
            return returnType;
        }

        // The name of the argument, i.e. 'T'
        var genericTypeName = ((TypeVariable) inputGenericType.get().getTypeArguments().get(0))
            .asElement()
            .getSimpleName();

        // If both elements are same generic items
        if (types.isSameType(types.erasure(returnType), types.erasure(adaptedType)) &&
            adaptedType instanceof DeclaredType declaredAdaptedType &&
            returnType instanceof DeclaredType declaredReturnType) {


            return findDeclaredGenericType(declaredAdaptedType, declaredReturnType, genericTypeName)
                .orElse(responseType);
        }
        return returnType;
    }

    /**
     * Selects all non-default adapters from the list of adapters.
     *
     * @param validAdapters the adapters to filter
     * @return the adapters that are not present in {@link DefaultAdapters}
     */
    private List<ReturnAdapterCall> findNonDefaultAdapters(List<ReturnAdapterCall> validAdapters) {
        return validAdapters.stream()
            .filter(
                call -> !types.isSameType(call.adapterClass().adapterType().asType(), defaultAdapterType)
            )
            .toList();
    }

    /**
     * Finds the matching generic type, used to select proper type if more than one generic is present.
     *
     * @param declaredAdaptedType the type returned by the adapter
     * @param declaredReturnType  the full return type from which to get the type
     * @param genericTypeName     the name of the generic parameter, i.e. 'T'
     * @return the type if found or empty
     */
    private Optional<TypeMirror> findDeclaredGenericType(
        DeclaredType declaredAdaptedType,
        DeclaredType declaredReturnType,
        Name genericTypeName
    ) {
        var typeArguments = declaredAdaptedType.getTypeArguments();
        for (int i = 0; i < typeArguments.size(); i++) {
            if (hasSameName(typeArguments.get(i), genericTypeName)) {
                return Optional.of(declaredReturnType.getTypeArguments().get(i));
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if typeArgument is a {@link TypeVariable} and the name matches provided name.
     *
     * @param typeArgument    the type argument to check
     * @param genericTypeName the name to match
     * @return true if typeArgument is a TypeVariable and its name matches the genericTypeName
     */
    private boolean hasSameName(TypeMirror typeArgument, Name genericTypeName) {
        if (!(typeArgument instanceof TypeVariable typeVariable)) return false;

        var name = typeVariable.asElement().getSimpleName();
        return name.equals(genericTypeName);
    }
}
