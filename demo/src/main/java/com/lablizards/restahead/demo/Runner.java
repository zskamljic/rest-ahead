package com.lablizards.restahead.demo;

import com.lablizards.restahead.RestAhead;
import com.lablizards.restahead.demo.clients.HttpBinMethodsService;

public class Runner {
    public static void main(String[] args) {
        var service = RestAhead.builder("https://httpbin.org")
            .build(HttpBinMethodsService.class);

        System.out.println(service.delete());
        System.out.println(service.get());
        System.out.println(service.patch());
        System.out.println(service.post());
        System.out.println(service.put());
    }
}
