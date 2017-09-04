package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jbellmann
 *
 */
@ConfigurationProperties(prefix = "zmon.oauth2.sso")
public class ZmonOAuth2Properties {

	private String clientId;
	private String clientSecret;

	private String authorizeUrl;
    private String accessTokenUrl;
    /**
     * additionalParams are propagated to spring-social-zauth and being used there as additional query params for requests
     * to Oauth endpoints.
     * The problem of "spring-social-zauth" library is that it uses single map of additionalParams for BOTH calls
     * to authorize URL AND accessToken URL. That library needs to be fixed in order to support 2 sets of additional
     * parameters.
     */
	private Map<String, String> additionalParams = new HashMap<>(0);

	/**
	 * Path to the directory where credential-files can be found (client.json).
	 */
	private String credentialsDirectoryPath;

	/**
	 * Name of the token used by the Platform
	 */
	private String platformTokenName;

	private boolean platformEnabled;

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


	public String getAuthorizeUrl() {
		return authorizeUrl;
	}

	public void setAuthorizeUrl(final String authorizeUrl) {
		this.authorizeUrl = authorizeUrl;
	}

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public void setAccessTokenUrl(final String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

	public String getPlatformTokenName() {
		return platformTokenName;
	}

	public void setPlatformTokenName(final String platformTokenName) {
		this.platformTokenName = platformTokenName;
	}

	public boolean isPlatformEnabled() {
		return platformEnabled;
	}

	public void setPlatformEnabled(final boolean platformEnabled) {
		this.platformEnabled = platformEnabled;
	}

    public Map<String, String> getAdditionalParams() {
        return additionalParams;
    }
}
