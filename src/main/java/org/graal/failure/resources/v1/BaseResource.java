package org.graal.failure.resources.v1;

import java.util.UUID;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.graal.failure.models.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseResource<T extends BaseModel> {

    private static final Logger logger = LoggerFactory.getLogger(BaseResource.class);


    @POST
    public Response post(T t) {
        return Response.ok().build();
    }

    @GET
    public Response getAll(@Context UriInfo info) {
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam UUID id) {
        return Response.ok().build();
    }

    @PUT
    public Response putTenant(T e) {
        return Response.ok().build();
    }

    @DELETE
    public Response delete(T t) {
        return Response.ok().build();
    }
}
