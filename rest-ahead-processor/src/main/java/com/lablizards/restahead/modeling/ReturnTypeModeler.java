package com.lablizards.restahead.modeling;

import com.lablizards.restahead.client.Response;
import com.lablizards.restahead.modeling.declaration.ReturnDeclaration;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Used for collecting return type converters and adapters.
 */
public class ReturnTypeModeler {
    private final Messager messager;
    private final TypeMirror futureType;
    private final DeclaredType defaultResponseType;
    private final Types types;

    public ReturnTypeModeler(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.types = types;
        var futureElement = elements.getTypeElement(Future.class.getCanonicalName());
        var responseType = elements.getTypeElement(Response.class.getCanonicalName())
            .asType();
        futureType = types.erasure(futureElement.asType());
        defaultResponseType = types.getDeclaredType(futureElement, responseType);
    }

    /**
     * Returns the return declaration if all data could be extracted.
     *
     * @param function the function from which to obtain the data
     * @return empty in case of errors, the declaration otherwise
     */
    public Optional<ReturnDeclaration> getReturnConfiguration(ExecutableElement function) {
        var returnType = function.getReturnType();

        if (returnType.getKind() == TypeKind.VOID) {
            return Optional.of(new ReturnDeclaration(Optional.of(returnType)));
        } else if (types.isSubtype(returnType, defaultResponseType)) {
            return Optional.of(new ReturnDeclaration(Optional.empty()));
        } else if (types.isSameType(futureType, types.erasure(returnType))) {
            return Optional.of(new ReturnDeclaration(Optional.of(((DeclaredType) returnType).getTypeArguments().get(0))));
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "No adapter to convert element from " + defaultResponseType + " to " + returnType, function);
            return Optional.empty();
        }
    }
}
