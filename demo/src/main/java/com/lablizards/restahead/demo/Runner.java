package com.lablizards.restahead.demo;

import com.lablizards.restahead.RestAhead;
import com.lablizards.restahead.demo.clients.HttpBinMethodsService;

import java.io.IOException;

public class Runner {
    public static void main(String[] args) throws IOException, InterruptedException {
        var service = RestAhead.builder("https://httpbin.org")
            .build(HttpBinMethodsService.class);

        System.out.println(service.delete());
        System.out.println(service.get());
        System.out.println(service.patch());
        System.out.println(service.post());
        System.out.println(service.put());
    }
}
