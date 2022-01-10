package io.github.zskamljic.restahead.modeling.declaration;

import javax.lang.model.element.TypeElement;
import java.util.List;

/**
 * Contains data relevant to the adapter.
 *
 * @param adapterType    the type of the adapter
 * @param adapterMethods the methods qualifying for use as an adapter
 */
public record AdapterClassDeclaration(
    TypeElement adapterType,
    List<AdapterMethodDeclaration> adapterMethods
) {
    public String variableName() {
        var typeName = adapterType.getSimpleName().toString();
        return typeName.substring(0, 1).toLowerCase() + typeName.substring(1);
    }
}
