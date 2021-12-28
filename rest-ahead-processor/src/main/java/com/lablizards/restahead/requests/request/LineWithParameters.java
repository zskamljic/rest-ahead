package com.lablizards.restahead.requests.request;

import java.util.List;

public record LineWithParameters(RequestLine requestLine, List<PresetQuery> parameters) {
}
