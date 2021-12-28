package com.lablizards.restahead.generation;

import com.lablizards.restahead.client.Response;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

/**
 * Create a response for the specified type.
 */
public class ResponseConverterGenerator {
    private final Messager messager;

    /**
     * Creates a new instance.
     *
     * @param messager the messager to report errors for
     */
    public ResponseConverterGenerator(Messager messager) {
        this.messager = messager;
    }

    /**
     * Generate an appropriate return statement for the type.
     *
     * @param returnType the type to return
     * @param builder    the method builder
     * @param function   the function for which code is generated
     */
    public void generateReturnStatement(TypeName returnType, MethodSpec.Builder builder, ExecutableElement function) {
        if (returnType.equals(TypeName.get(Response.class))) {
            builder.addStatement("return response");
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "Convert type " + returnType + " not supported", function);
        }
    }
}
