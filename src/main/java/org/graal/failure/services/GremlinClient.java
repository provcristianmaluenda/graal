package org.graal.failure.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import io.quarkus.runtime.configuration.ProfileManager;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GremlinClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(GremlinClient.class);

    @ConfigProperty(name = "graph.host")
    String host;

    @ConfigProperty(name = "graph.port", defaultValue = "8182")
    Integer port;

    @ConfigProperty(name = "graph.maxcontentlength", defaultValue = "1048576")
    Integer maxContentLength;

    protected Cluster cluster;
    protected GraphTraversalSource g;

    @PostConstruct
    protected void init() {
        logger.info("Connecting to Graph Database {}:{}", host, port);
        logger.info("MaxContentLength: {}", maxContentLength);
        Cluster.Builder build = Cluster.build()
                .addContactPoint(host)
                .port(port)
                .serializer(Serializers.GRAPHSON_V3D0)
                .maxContentLength(maxContentLength);
        if (!"dev".equals(ProfileManager.getActiveProfile())) {
            build.enableSsl(true);
        }
        cluster = build.create();

        g = AnonymousTraversalSource.traversal().withRemote(DriverRemoteConnection.using(cluster));
    }

    public GraphTraversalSource getG() {
        return g;
    }

    @Override
    public void close() throws Exception {
        g.close();
        cluster.close();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
