package com.lablizards.restahead.modeling.validation;

import com.lablizards.restahead.modeling.declaration.ParameterDeclaration;
import com.lablizards.restahead.requests.request.PresetQuery;
import com.lablizards.restahead.requests.request.RequestLine;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Used to validate path parameter of HTTP verb annotations.
 */
public class PathValidator {
    private final Messager messager;

    /**
     * Create a new instance with messager that will receive all errors.
     *
     * @param messager the messager to report errors to
     */
    public PathValidator(Messager messager) {
        this.messager = messager;
    }

    /**
     * Check if any errors are present in the path, or if the function does not match required format.
     * Updates the data if needed and constructs a final request spec.
     *
     * @param function    the function for which path is validated
     * @param requestLine the request line being validated
     * @param parameters  the parameters in the given request
     * @return empty optional in case of errors or requestSpec if no errors are discovered
     */
    public Optional<RequestLine> validatePathAndExtractQuery(
        ExecutableElement function,
        RequestLine requestLine,
        ParameterDeclaration parameters
    ) {
        if (requestLine.path() == null || requestLine.path().isEmpty()) {
            return Optional.of(requestLine);
        }

        try {
            var uri = new URI(requestLine.path());
            var query = uri.getQuery();
            if (query != null) {
                requestLine = new RequestLine(requestLine.request(), uri.getPath());
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
        } catch (URISyntaxException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), function);
            return Optional.empty();
        }
        return Optional.of(requestLine);
    }
}
