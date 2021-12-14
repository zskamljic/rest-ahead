package com.lablizards.restahead.generation;

import com.lablizards.restahead.client.Response;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

public class ResponseConverterGenerator {
    private final Messager messager;

    public ResponseConverterGenerator(Messager messager) {
        this.messager = messager;
    }

    public void generateReturnStatement(TypeName returnType, MethodSpec.Builder builder, ExecutableElement function) {
        if (returnType.equals(TypeName.get(Response.class))) {
            builder.addStatement("return response");
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "Convert type " + returnType + " not supported", function);
        }
    }
}
