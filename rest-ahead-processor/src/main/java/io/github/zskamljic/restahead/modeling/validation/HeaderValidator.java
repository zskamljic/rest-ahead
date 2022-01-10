package io.github.zskamljic.restahead.modeling.validation;

import io.github.zskamljic.restahead.modeling.declaration.RequestParameterSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Validates the headers and creates corresponding {@link RequestParameterSpec}
 */
public class HeaderValidator extends CommonParameterValidator {

    // Valid HTTP header characters as per RFC2616, page 31
    private static final Predicate<String> HEADER_REGEX = Pattern.compile("[!-'*+\\-./\\dA-Z^_`a-z|~]+")
        .asMatchPredicate()
        .negate();

    /**
     * Create a new instance.
     *
     * @param messager the messager where errors will be reported to
     * @param elements the Elements object used to fetch type info
     * @param types    an instance of the Types utility
     */
    public HeaderValidator(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    /**
     * Get a header spec if the variable is a valid request parameter
     *
     * @param value     the header name
     * @param parameter the parameter to generate the header from
     * @return Optional.empty in case of errors, HeaderSpec otherwise
     */
    public Optional<RequestParameterSpec> getHeaderSpec(String value, VariableElement parameter) {
        if (value.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Generating header names from parameter names not yet supported", parameter);
            return Optional.empty();
        }

        if (HEADER_REGEX.test(value)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Header contains illegal characters", parameter);
            return Optional.empty();
        }

        return extractSpec(parameter, value);
    }
}
