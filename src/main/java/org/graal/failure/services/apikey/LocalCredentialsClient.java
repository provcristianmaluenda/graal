package org.graal.failure.services.apikey;

import org.eclipse.microprofile.config.ConfigProvider;

public class LocalCredentialsClient implements CredentialsClient {

    /**
     * This method is only for local development.
     *
     * @param key
     * @return a mock API Key
     */
    public String getSecret(final String key) {
        return ConfigProvider.getConfig().getValue(key, String.class);
    }
}
