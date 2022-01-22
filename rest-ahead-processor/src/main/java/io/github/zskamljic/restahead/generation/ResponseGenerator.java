package io.github.zskamljic.restahead.generation;

import com.squareup.javapoet.MethodSpec;
import io.github.zskamljic.restahead.modeling.declaration.ReturnAdapterCall;
import io.github.zskamljic.restahead.modeling.declaration.ReturnDeclaration;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Create a response for the specified type.
 */
public class ResponseGenerator {
    private final ExceptionsGenerator exceptionsGenerator;
    private final ConversionGenerator conversionGenerator = new ConversionGenerator();

    public ResponseGenerator(ExceptionsGenerator exceptionsGenerator) {
        this.exceptionsGenerator = exceptionsGenerator;
    }

    /**
     * Generate an appropriate return statement
     *
     * @param returnDeclaration  the declaration based on which the statement should be generated
     * @param declaredExceptions exceptions declared by the service that don't need to be caught
     * @param builder            the builder to which the return statement should be added
     */
    public void generateReturnStatement(
        ReturnDeclaration returnDeclaration,
        List<TypeMirror> declaredExceptions,
        MethodSpec.Builder builder
    ) {
        if (returnDeclaration.targetConversion().isEmpty() && returnDeclaration.adapterCall().isEmpty()) {
            builder.addStatement("return $L", Variables.RESPONSE);
            return;
        }
        returnDeclaration.targetConversion()
            .ifPresent(returnType -> conversionGenerator.generateConversion(builder, returnType));

        var returnedName = getReturnedVariableName(returnDeclaration.targetConversion().isPresent());
        returnDeclaration.adapterCall().ifPresentOrElse(
            call -> createAdapterCode(call, declaredExceptions, builder, returnedName),
            () -> builder.addStatement("return $L", returnedName)
        );
    }

    /**
     * Create the code for calling the adapter before returning.
     *
     * @param call               the call to adapter
     * @param declaredExceptions the exceptions present on function declaration
     * @param builder            the method builder
     * @param responseName       the name of response variable
     */
    private void createAdapterCode(
        ReturnAdapterCall call,
        List<TypeMirror> declaredExceptions,
        MethodSpec.Builder builder,
        String responseName
    ) {
        var expectedExceptions = call.adapterMethod()
            .exceptions();
        exceptionsGenerator.generateTryCatchIfNeeded(builder, declaredExceptions, expectedExceptions,
            () -> {
                var statement = "$L.$L($L)";
                if (call.adapterMethod().returnType().getKind() != TypeKind.VOID) {
                    statement = "return " + statement;
                }
                builder.addStatement(statement, call.adapterClass().variableName(), call.adapterMethod().name(), responseName);
            }
        );
    }

    /**
     * Select the name of the variable to return, if value was not converted, no new variable has been created.
     *
     * @param isConverted if the response has been converted
     * @return the name of the variable
     */
    private String getReturnedVariableName(boolean isConverted) {
        return isConverted ? Variables.CONVERTED_NAME : Variables.RESPONSE;
    }
}
