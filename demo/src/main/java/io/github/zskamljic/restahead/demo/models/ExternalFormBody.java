package io.github.zskamljic.restahead.demo.models;

import io.github.zskamljic.restahead.annotations.form.FormName;

public record ExternalFormBody(
    @FormName("snake_case") String field
) {
}
