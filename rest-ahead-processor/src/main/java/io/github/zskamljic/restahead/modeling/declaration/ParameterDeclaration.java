package io.github.zskamljic.restahead.modeling.declaration;

import io.github.zskamljic.restahead.encoding.BodyEncoding;
import io.github.zskamljic.restahead.request.PresetValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parameter holder.
 *
 * @param headers       the header parts
 * @param query         the query parts
 * @param paths         the path parts
 * @param body          the body encoding
 * @param presetQueries the queries present in request line
 * @param presetHeaders the headers present in request line
 */
public record ParameterDeclaration(
    List<RequestParameterSpec> headers,
    List<RequestParameterSpec> query,
    List<RequestParameterSpec> paths,
    Optional<BodyEncoding> body,
    List<PresetValue> presetQueries,
    List<PresetValue> presetHeaders
) {
    public ParameterDeclaration(
        List<RequestParameterSpec> headers,
        List<RequestParameterSpec> query,
        List<RequestParameterSpec> paths,
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<BodyEncoding> body
    ) {
        this(headers, query, paths, body, new ArrayList<>(), new ArrayList<>());
    }
}
