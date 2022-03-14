package io.github.zskamljic.restahead.demo.jaxrs;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.Map;

public interface JaxRsService {
    @GET
    @Path(("/get/{something}"))
    Map<String, Object> performGet(@PathParam("something") String something);
}
