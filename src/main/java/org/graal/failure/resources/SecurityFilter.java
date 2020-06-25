package org.graal.failure.resources;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import org.apache.commons.lang3.StringUtils;
import org.graal.failure.services.apikey.CredentialsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.util.Map;
import java.util.Set;

@Provider
public class SecurityFilter implements ContainerRequestFilter {
    final private static Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    final public static String APIKEY = "API-Key";

    enum Role {
        reader,
        contributor,
        contributorAdmin,
        admin,
        unknown
    }

    @Inject
    CredentialsClient credentialsClient;
    @Context
    UriInfo info;
    @Context
    HttpServerRequest request;
    Map<String, Role> keys;
    Set<String> observabilityDataPath;

    public SecurityFilter() {
        observabilityDataPath = Set.of(
                "/v1/charts",
                "/v1/umbrellas",
                "/v1/deployments");
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String path = info.getPath();
        if ("/healthcheck".equals(path)) {
            return;
        }
        final HttpMethod method = request.method();
        final String apiKey = requestContext.getHeaderString(APIKEY);
        if (keys == null) {
            initKeys();
        }
        Role role = getRole(apiKey);
        if (!validateAccess(role, method, path)) {
            Response.Status status = Response.Status.UNAUTHORIZED;
            requestContext.abortWith(
                    Response.serverError().build());
        }
    }

    protected boolean validateAccess(Role role, HttpMethod method, String path) {
        logger.debug("Role: {}, Method: {}, Path: {}", role, method, path);
        boolean hasAccess = false;
        switch (role) {
        case admin:
            hasAccess = true;
        case contributorAdmin:
            if ((method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT)) &&
                    observabilityDataPath.contains(path)) {
                hasAccess = true;
            }
        case contributor:
            if (method.equals(HttpMethod.POST) && observabilityDataPath.contains(path)) {
                hasAccess = true;
            }
        case reader:
            if (method.equals(HttpMethod.GET)) {
                hasAccess = true;
            }
        }
        return hasAccess;
    }

    protected Role getRole(String apiKey) {
        if (StringUtils.isEmpty(apiKey)) {
            return Role.unknown;
        }
        return keys.getOrDefault(apiKey, Role.unknown);
    }

    protected void initKeys() {
    }
}
