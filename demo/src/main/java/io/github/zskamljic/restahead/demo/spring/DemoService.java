package io.github.zskamljic.restahead.demo.spring;

import io.github.zskamljic.restahead.JacksonConverter;
import io.github.zskamljic.restahead.annotations.verbs.Get;
import io.github.zskamljic.restahead.spring.RestAheadService;

import java.util.Map;

/**
 * Service that will be automatically injected as a bean.
 */
@RestAheadService(url = "https://httpbin.org", converter = JacksonConverter.class)
public interface DemoService {
    @Get("/get")
    Map<String, Object> performGet();
}
