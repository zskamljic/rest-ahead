package com.lablizards.restahead.modeling.validation;

import com.lablizards.restahead.modeling.declaration.RequestParameterSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Optional;

/**
 * Used to validate queries and obtain parameter info.
 */
public class QueryValidator extends CommonParameterValidator {

    /**
     * Construct a new instance.
     *
     * @param messager     the message to send errors to
     * @param elements the elements to use for class lookup
     * @param types        an instance of Types utility
     */
    public QueryValidator(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    /**
     * Get the query info.
     *
     * @param value     the annotation value
     * @param parameter the parameter where to extract type, name etc. from
     * @return empty if errors were present, non-empty otherwise
     */
    public Optional<RequestParameterSpec> getQuerySpec(String value, VariableElement parameter) {
        if (value.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Generating query parameter from function parameter names not yet supported", parameter);
            return Optional.empty();
        }

        return extractSpec(parameter, value);
    }
}
