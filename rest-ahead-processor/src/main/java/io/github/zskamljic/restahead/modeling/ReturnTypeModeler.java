package io.github.zskamljic.restahead.modeling;

import io.github.zskamljic.restahead.adapter.DefaultAdapters;
import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.client.responses.BodyAndErrorResponse;
import io.github.zskamljic.restahead.client.responses.BodyResponse;
import io.github.zskamljic.restahead.client.responses.Response;
import io.github.zskamljic.restahead.modeling.conversion.BodyAndErrorConversion;
import io.github.zskamljic.restahead.modeling.conversion.BodyResponseConversion;
import io.github.zskamljic.restahead.modeling.conversion.Conversion;
import io.github.zskamljic.restahead.modeling.conversion.DirectConversion;
import io.github.zskamljic.restahead.modeling.conversion.OptionsConversion;
import io.github.zskamljic.restahead.modeling.declaration.AdapterClassDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.AdapterMethodDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.ReturnAdapterCall;
import io.github.zskamljic.restahead.modeling.declaration.ReturnDeclaration;

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
import java.util.concurrent.CompletableFuture;
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
    private final TypeMirror bodyResponseType;
    private final TypeMirror bodyAndErrorType;
    private final TypeMirror verbListType;
    private final TypeMirror voidMirror;

    public ReturnTypeModeler(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.types = types;

        var listType = elements.getTypeElement(List.class.getName());
        var verbType = elements.getTypeElement(Verb.class.getName()).asType();
        verbListType = types.getDeclaredType(listType, verbType);
        voidMirror = elements.getTypeElement(Void.class.getName()).asType();

        var futureElement = elements.getTypeElement(CompletableFuture.class.getCanonicalName());
        responseType = elements.getTypeElement(Response.class.getCanonicalName())
            .asType();
        futureType = types.erasure(elements.getTypeElement(Future.class.getCanonicalName()).asType());
        defaultResponseType = types.getDeclaredType(futureElement, responseType);
        defaultAdapterType = elements.getTypeElement(DefaultAdapters.class.getCanonicalName()).asType();
        bodyResponseType = types.erasure(elements.getTypeElement(BodyResponse.class.getCanonicalName()).asType());
        bodyAndErrorType = types.erasure(elements.getTypeElement(BodyAndErrorResponse.class.getCanonicalName()).asType());
    }

    /**
     * Returns the return declaration if all data could be extracted.
     *
     * @param function the function from which to obtain the data
     * @param adapters the return type adapters
     * @param verb     the verb for current call
     * @return empty in case of errors, the declaration otherwise
     */
    public Optional<ReturnDeclaration> getReturnConfiguration(
        ExecutableElement function,
        List<AdapterClassDeclaration> adapters,
        Verb verb
    ) {
        var returnType = function.getReturnType();

        Optional<Conversion> conversion;
        Optional<ReturnAdapterCall> adapterCall;
        if (types.isAssignable(defaultResponseType, returnType)) {
            conversion = Optional.empty();
            adapterCall = Optional.empty();
        } else if (types.isSubtype(types.erasure(returnType), futureType)) {
            var convertedType = ((DeclaredType) returnType).getTypeArguments().get(0);
            conversion = Optional.of(selectConversion(convertedType, verb));
            adapterCall = Optional.empty();
        } else {
            adapterCall = findCorrectAdapter(adapters, returnType);
            if (adapterCall.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR, "No adapter to convert element from " + defaultResponseType + " to " + returnType, function);
                return Optional.empty();
            }

            var adaptedConvertType = findAdaptedConvertType(returnType, adapterCall.get());
            if (types.isSameType(adaptedConvertType, responseType) || adaptedConvertType.getKind() == TypeKind.VOID) {
                conversion = Optional.empty();
            } else {
                conversion = Optional.of(selectConversion(adaptedConvertType, verb));
            }
        }

        if (hasInvalidReturnType(verb, conversion.orElse(null), adapterCall.orElse(null), function)) {
            return Optional.empty();
        }

        return Optional.of(new ReturnDeclaration(conversion, adapterCall));
    }

    /**
     * Checks if the discovered return type is allowed for requests (for example, HEAD should not have a body)
     *
     * @param verb        the verb used in the request
     * @param conversion  the conversion required for this endpoint
     * @param adapterCall the adapter call for this endpoint
     * @param function    the function that the error should be reported on
     * @return whether the return type is valid for request
     */
    private boolean hasInvalidReturnType(Verb verb, Conversion conversion, ReturnAdapterCall adapterCall, ExecutableElement function) {
        if (verb != Verb.HEAD && verb != Verb.OPTIONS || conversion == null) return false;

        if (adapterCall != null && adapterCall.adapterMethod().returnType().getKind() == TypeKind.VOID) {
            return false;
        }

        TypeMirror mirror;
        if (conversion instanceof BodyResponseConversion body) {
            mirror = body.targetType();
        } else if (conversion instanceof DirectConversion direct) {
            mirror = direct.targetType();
        } else {
            return !(conversion instanceof OptionsConversion);
        }

        if (types.isSameType(voidMirror, mirror)) {
            return false;
        }

        if (verb == Verb.OPTIONS && types.isAssignable(mirror, verbListType)) {
            return false;
        }

        if (verb == Verb.HEAD) {
            messager.printMessage(Diagnostic.Kind.ERROR, "HEAD request responses should be of type BodyResponse<Void> or void", function);
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "OPTIONS request responses should be of type BodyResponse<Void>, void or List<Verb>", function);
        }
        return true;
    }

    /**
     * Select the conversion to use for specified type.
     *
     * @param convertedType the type being converted
     * @param verb          the verb for endpoint
     * @return appropriate strategy for type conversion
     */
    private Conversion selectConversion(TypeMirror convertedType, Verb verb) {
        if (verb == Verb.OPTIONS && types.isAssignable(convertedType, verbListType)) {
            return new OptionsConversion(verbListType);
        } else if (types.isSameType(types.erasure(convertedType), bodyResponseType)) {
            var argument = ((DeclaredType) convertedType).getTypeArguments().get(0);
            return new BodyResponseConversion(argument);
        } else if (types.isSameType(types.erasure(convertedType), bodyAndErrorType)) {
            var arguments = ((DeclaredType) convertedType).getTypeArguments();
            return new BodyAndErrorConversion(arguments.get(0), arguments.get(1));
        } else {
            return new DirectConversion(convertedType);
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
