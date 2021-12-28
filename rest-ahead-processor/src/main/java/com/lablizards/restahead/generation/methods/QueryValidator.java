package com.lablizards.restahead.generation.methods;

import com.lablizards.restahead.requests.parameters.RequestParameterSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Optional;

public class QueryValidator extends CommonParameterValidator {

    public QueryValidator(Messager messager, Elements elementUtils, Types types) {
        super(messager, elementUtils, types);
    }

    public Optional<RequestParameterSpec> getQuerySpec(String value, VariableElement parameter) {
        if (value.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Generating query parameter from function parameter names not yet supported", parameter);
            return Optional.empty();
        }

        return extractSpec(parameter, value);
    }
}
