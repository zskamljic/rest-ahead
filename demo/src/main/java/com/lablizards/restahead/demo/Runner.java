package com.lablizards.restahead.demo;

import com.lablizards.restahead.RestAhead;
import com.lablizards.restahead.demo.clients.HttpBinMethodsService;

public class Runner {
    public static void main(String[] args) {
        var service = RestAhead.builder("https://httpbin.org")
            .build(HttpBinMethodsService.class);

        service.delete();
        service.get();
        service.patch();
        service.post();
        service.put();
    }
}
