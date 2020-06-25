package org.graal.failure.services.apikey;

public interface CredentialsClient {

    String getSecret(String key);
}
