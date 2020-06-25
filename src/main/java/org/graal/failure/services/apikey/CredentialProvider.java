package org.graal.failure.services.apikey;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class CredentialProvider {
    private final static Logger logger = LoggerFactory.getLogger(CredentialProvider.class);

    @ConfigProperty(name = "aws.region")
    String region;

    @Produces
    @IfBuildProfile("dev")
    public CredentialsClient localDevelopment() {
        logger.info("Credentials provider: LocalCredentialsClient");
        return new LocalCredentialsClient();
    }

    @Produces
    @IfBuildProfile("test")
    public CredentialsClient localDevelopmentTest() {
        logger.info("Credentials provider: LocalCredentialsClient test");
        return new LocalCredentialsClient();
    }

    @Produces
    @DefaultBean
    public CredentialsClient awsProvider() {
        logger.info("Credentials provider: AWSCredentialsClient [{}]", region);
        return new AWSCredentialsClient(region);
    }

}
