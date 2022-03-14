package io.github.zskamljic.restahead.demo.spring;

import io.github.zskamljic.restahead.client.responses.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public interface SpringService {
    @GetMapping("/get")
    Response performGet(@RequestHeader("Authorization") String value);
}
