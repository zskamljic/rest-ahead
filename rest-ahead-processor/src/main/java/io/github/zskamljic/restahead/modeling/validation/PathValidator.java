package io.github.zskamljic.restahead.modeling.validation;

import io.github.zskamljic.restahead.modeling.declaration.ParameterDeclaration;
import io.github.zskamljic.restahead.modeling.declaration.RequestParameterSpec;
import io.github.zskamljic.restahead.requests.request.BasicRequestLine;
import io.github.zskamljic.restahead.requests.request.PresetQuery;
import io.github.zskamljic.restahead.requests.request.RequestLine;
import io.github.zskamljic.restahead.requests.request.path.RequestPath;
import io.github.zskamljic.restahead.requests.request.path.StringPath;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Used to validate path parameter of HTTP verb annotations.
 */
public class PathValidator extends CommonParameterValidator {
    public PathValidator(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    /**
     * Check if any errors are present in the path, or if the function does not match required format.
     * Updates the data if needed and constructs a final request spec.
     *
     * @param function    the function for which path is validated
     * @param requestLine the request line being validated
     * @param parameters  the parts in the given request
     * @return empty optional in case of errors or requestSpec if no errors are discovered
     */
    public Optional<RequestLine> validatePathAndExtractQuery(
        ExecutableElement function,
        BasicRequestLine requestLine,
        ParameterDeclaration parameters
    ) {
        if (requestLine.path() == null || requestLine.path().isEmpty()) {
            return Optional.of(new RequestLine(requestLine.verb(), new StringPath("")));
        }

        var path = RequestPath.parse(requestLine.path());
        try {
            var uri = path.uri();
            var query = uri.getQuery();
            extractQuery(parameters, query, function);
        } catch (URISyntaxException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), function);
            return Optional.empty();
        }
        var actualLine = new RequestLine(requestLine.verb(), path);
        return Optional.of(actualLine);
    }

    /**
     * Extracts the preset query parts from the request.
     *
     * @param parameters the parts to store query items in
     * @param query      the query string
     * @param function   the function on which to report an error
     */
    private void extractQuery(ParameterDeclaration parameters, String query, ExecutableElement function) {
        if (query == null) return;

        var items = query.split("&");
        for (var item : items) {
            var nameValue = item.split("=");
            if (nameValue.length != 2) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Malformed query", function);
                continue;
            }
            parameters.presetQueries().add(new PresetQuery(nameValue[0], nameValue[1]));
        }
    }

    /**
     * Get the parameter from the variable.
     *
     * @param value     the value of the annotation, to be used as path placeholder
     * @param parameter the parameter to extract data from
     * @return the spec if no errors are present, empty otherwise
     */
    public Optional<RequestParameterSpec> getPathSpec(String value, VariableElement parameter) {
        if (value.isEmpty()) {
            value = parameter.getSimpleName().toString();
        }
        return extractSpec(parameter, value)
            .filter(spec -> {
                if (spec.isIterable()) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Path parts must be singular.");
                    return false;
                }
                return true;
            });
    }
}
