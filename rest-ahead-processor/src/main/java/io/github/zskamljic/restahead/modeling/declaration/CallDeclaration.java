package io.github.zskamljic.restahead.modeling.declaration;

import io.github.zskamljic.restahead.encoding.FormBodyEncoding;
import io.github.zskamljic.restahead.request.RequestLine;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * The call as declared by the interface
 *
 * @param function          the function that needs to be overridden
 * @param exceptions        the exceptions thrown by this call
 * @param requestLine       the request and path to be used
 * @param parameters        the parts of the request
 * @param returnDeclaration the return value of the request
 */
public record CallDeclaration(
    ExecutableElement function,
    List<TypeMirror> exceptions,
    RequestLine requestLine,
    ParameterDeclaration parameters,
    ReturnDeclaration returnDeclaration
) {
    /**
     * Whether the body requires a converter to be present in the service.
     *
     * @return true if a converter is required, false otherwise
     */
    public boolean requiresConverter() {
        return parameters.body().isPresent() && !(parameters.body().get() instanceof FormBodyEncoding) ||
            returnDeclaration.targetConversion().isPresent();
    }
}
