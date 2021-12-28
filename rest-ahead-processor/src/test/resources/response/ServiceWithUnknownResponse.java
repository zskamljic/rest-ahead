package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.Delete;

public interface ServiceWithUnknownResponse {
    @Delete("/delete")
    TestResponse delete();

    record TestResponse() {
    }
}