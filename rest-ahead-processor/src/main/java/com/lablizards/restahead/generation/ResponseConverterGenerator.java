package com.lablizards.restahead.generation;

import com.lablizards.restahead.client.Response;
import com.lablizards.restahead.conversion.GenericReference;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Create a response for the specified type.
 */
public class ResponseConverterGenerator {
    private final TypeMirror responseType;

    /**
     * Creates a new instance.
     *
     * @param elementUtils the Elements to use for class lookup
     */
    public ResponseConverterGenerator(Elements elementUtils) {
        responseType = elementUtils.getTypeElement(Response.class.getCanonicalName())
            .asType();
    }

    /**
     * Generate an appropriate return statement for the type.
     *
     * @param returnType the type to return
     * @param builder    the method builder
     * @param function   the function for which code is generated
     */
    public void generateReturnStatement(TypeMirror returnType, MethodSpec.Builder builder, ExecutableElement function) {
        if (returnType.equals(responseType)) {
            builder.addStatement("return response");
        } else if (isSimpleType(returnType)) {
            builder.addStatement("return converter.deserialize(response, $T.class)", returnType);
        } else {
            builder.addStatement("var responseTypeReference = new $T<$T>(){}", GenericReference.class, returnType);
            builder.addStatement("return converter.deserialize(response, responseTypeReference.getType())");
        }
    }

    private boolean isSimpleType(TypeMirror returnType) {
        if (!(returnType instanceof DeclaredType declaredType)) {
            return true;
        }
        return declaredType.getTypeArguments().isEmpty();
    }
}
