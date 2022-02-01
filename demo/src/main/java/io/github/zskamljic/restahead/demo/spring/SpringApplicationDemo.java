package io.github.zskamljic.restahead.demo.spring;

import io.github.zskamljic.restahead.spring.EnableRestAhead;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring boot app runner.
 */
@EnableRestAhead
@SpringBootApplication
public class SpringApplicationDemo {
    public static void main(String[] args) {
        SpringApplication.run(SpringApplicationDemo.class, args);
    }
}
