package org.graal.failure.services.apikey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.*;

public class AWSCredentialsClient implements CredentialsClient {
    private final static Logger logger = LoggerFactory.getLogger(AWSCredentialsClient.class);

    private SecretsManagerClient client;

    public AWSCredentialsClient(final String region) {
        Region rgn = Region.of(region);
        client = SecretsManagerClient.builder()
                .region(rgn)
                .httpClientBuilder(ApacheHttpClient.builder())
                .build();
    }

    @Override
    public String getSecret(final String key) {
        final GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest
                .builder()
                .secretId(key)
                .build();
        GetSecretValueResponse secretValueResponse;
        try {
            secretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException e) {
            logger.error("Secrets Manager can't decrypt.", e);
            throw new RuntimeException("Secrets Manager can't decrypt.");
        } catch (InternalServiceErrorException e) {
            logger.error("An error occurred on the server side.", e);
            throw new RuntimeException("An error occurred on the server side.");
        } catch (InvalidParameterException e) {
            logger.error("You provided an invalid value for a parameter.", e);
            throw new RuntimeException("You provided an invalid value for a parameter.");
        } catch (InvalidRequestException e) {
            logger.error("You provided a parameter value that is not valid for the current state of the resource.", e);
            throw new RuntimeException(
                    "You provided a parameter value that is not valid for the current state of the resource.");
        } catch (ResourceNotFoundException e) {
            logger.error("We can't find the resource that you asked for: " + key, e);
            throw new RuntimeException("We can't find the resource that you asked for: " + key);
        }
        return secretValueResponse.secretString();
    }
}
