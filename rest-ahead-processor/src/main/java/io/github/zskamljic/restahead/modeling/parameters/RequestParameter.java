package io.github.zskamljic.restahead.modeling.parameters;

public record RequestParameter(Type type, String value) {
    public enum Type {
        HEADER, QUERY, PATH
    }
}
