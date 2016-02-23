package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @author jbellmann
 *
 */
@ConfigurationProperties(prefix = "zmon.oauth2.sso")
public class ZmonOAuth2Properties {

	private String clientId;

	private String clientSecret;

    /**
     * Path to the directory where credential-files can be found (client.json).
     * 
     */
    private String credentialsDirectoryPath;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

    public String getCredentialsDirectoryPath() {
        return credentialsDirectoryPath;
    }

    public void setCredentialsDirectoryPath(String credentialsDirectoryPath) {
        this.credentialsDirectoryPath = credentialsDirectoryPath;
    }

}
