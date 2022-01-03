package com.lablizards.restahead.modeling.declaration;

import com.lablizards.restahead.requests.request.RequestLine;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * The call as declared by the interface
 *
 * @param function          the function that needs to be overridden
 * @param exceptions        the exceptions thrown by this call
 * @param requestLine       the request and path to be used
 * @param parameters        the parameters of the request
 * @param returnDeclaration the return value of the request
 */
public record CallDeclaration(
    ExecutableElement function,
    List<TypeMirror> exceptions,
    RequestLine requestLine,
    ParameterDeclaration parameters,
    ReturnDeclaration returnDeclaration
) {
}
