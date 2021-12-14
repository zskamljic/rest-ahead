package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.client.Response;

public interface ServiceWithResponse {
    @Delete("/delete")
    Response delete();
}