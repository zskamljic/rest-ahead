package restahead.jaxrs;

import io.github.zskamljic.restahead.client.requests.Verb;
import io.github.zskamljic.restahead.client.responses.Response;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;

public interface JaxRsService {
    @DELETE
    @Path("/delete")
    Response performDelete(
        @HeaderParam("custom") String header1,
        @HeaderParam("header2") String header2
    );

    @GET
    @Path("/{get}/{hello}")
    Response performGet(
        @PathParam("") String get,
        @PathParam("hello") String second
    );

    @HEAD
    @Path("/{get}/{hello}")
    Response performHead(
        @PathParam("") String get,
        @PathParam("hello") String second
    );

    @OPTIONS
    @Path("/{get}/{hello}")
    List<Verb> performOptions(
        @PathParam("") String get,
        @PathParam("hello") String second
    );

    @PATCH
    @Path("/patch")
    Response performPatch();

    @POST
    @Path("/post")
    Response performPost();

    @PUT
    @Path("/put")
    @Consumes("application/json")
    @Produces("text/plain")
    Response performPut();

    record FormBody(String field) {
    }
}