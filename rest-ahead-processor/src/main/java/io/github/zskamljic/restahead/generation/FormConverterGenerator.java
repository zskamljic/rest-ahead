package io.github.zskamljic.restahead.generation;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.github.zskamljic.restahead.encoding.FormEncoding;
import io.github.zskamljic.restahead.encoding.generation.GenerationStrategy;
import io.github.zskamljic.restahead.modeling.declaration.BodyDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.CallDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.ServiceDeclaration;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Generates the FormConverter class if necessary.
 */
public class FormConverterGenerator {
    private final Messager messager;
    private final Filer filer;

    public FormConverterGenerator(Messager messager, Filer filer) {
        this.messager = messager;
        this.filer = filer;
    }

    /**
     * Checks if the class needs to be generated and generate it if needed.
     *
     * @param serviceDeclarations the services to check for any form data.
     */
    public void generateFormEncoderIfNeeded(List<ServiceDeclaration> serviceDeclarations) {
        var formEncodableParameters = serviceDeclarations.stream()
            .map(ServiceDeclaration::calls)
            .flatMap(Collection::stream)
            .map(CallDeclaration::parameters)
            .map(ParameterDeclaration::body)
            .flatMap(Optional::stream)
            .map(BodyDeclaration::encoding)
            .filter(FormEncoding.class::isInstance)
            .map(FormEncoding.class::cast)
            .toList();
        if (formEncodableParameters.isEmpty()) {
            return;
        }

        var encoder = generateFormEncoder(formEncodableParameters);
        var javaFile = JavaFile.builder(FormConverterGenerator.class.getPackageName(), encoder)
            .indent("    ")
            .build();

        try {
            javaFile.writeTo(filer);
            messager.printMessage(Diagnostic.Kind.NOTE, "Generated " + encoder.name);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Unable to write class: " + e.getMessage());
        }
    }

    /**
     * Generates the actual implementation.
     *
     * @param parameters the parameters that require generation
     * @return the generated type.
     */
    private TypeSpec generateFormEncoder(List<FormEncoding> parameters) {
        var typeToStrategy = new HashMap<TypeMirror, GenerationStrategy>();
        for (var parameter : parameters) {
            typeToStrategy.put(parameter.strategy().type(), parameter.strategy());
        }

        var typeBuilder = TypeSpec.classBuilder(Variables.FORM_CONVERTER)
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC);

        var methods = typeToStrategy.values()
            .stream()
            .map(GenerationStrategy::generateMethod);
        typeBuilder.addMethods(methods::iterator);

        return typeBuilder.build();
    }
}
