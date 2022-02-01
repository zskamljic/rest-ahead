package io.github.zskamljic.restahead.demo.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple controller to showcase usage in spring boot app
 */
@RestController
public class DemoController {
    /**
     * Field is package private so it's accessible from tests.
     */
    final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    /**
     * Proxy httpbin.org through RestAhead
     *
     * @return whatever the service returns
     */
    @GetMapping("/get")
    public Object performGet() {
        return demoService.performGet();
    }
}
