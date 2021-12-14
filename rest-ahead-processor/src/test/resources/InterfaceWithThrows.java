package com.lablizards.restahead.demo;

import com.lablizards.restahead.annotations.verbs.Delete;
import com.lablizards.restahead.client.Response;

import java.io.IOException;

public interface InterfaceWithThrows {
    @Delete("/delete")
    Response delete() throws IOException;
}