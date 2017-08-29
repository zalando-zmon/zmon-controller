package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
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
     * Knowing this limitation, a decision was taken to extract additional parameters from authorizeUrl only. Thus, if
     * "authorizeUrl" is configured as "https://foo.bar/baz?param1=bro&param2=pro" then "param1=bro&param2=pro" part
     * will be used for additionalParams. Query parameters of accessTokenUrl ARE IGNORED.
     */
    private Map<String, String> additionalParams;

	/**
	 * Path to the directory where credential-files can be found (client.json).
	 */
	private String credentialsDirectoryPath;

	/**
	 * Name of the token used by the Platform
	 */
	private String platformTokenName;

	private boolean platformEnabled;

    @PostConstruct
    public void init() {
        additionalParams = UriComponentsBuilder.fromHttpUrl(authorizeUrl)
                .build()
                .getQueryParams()
                .toSingleValueMap();

        /**
         * Following URLs are being used by "spring-social-zauth" library which expects only base URLs without
         * additional query parameters. Thus, all possible query parameters are stripped.
         *
         * Problem of "spring-social-zauth" is that it appends "?client-id=" to the URL. Thus, if one provides an URL
         * like "https://foo.bar/baz?param1=bro", the library will transform it to
         * "https://foo.bar/baz?param1=bro?client-id=...".
         *
         * This hack will be here until "spring-social-zauth" is fixed
         */
        authorizeUrl = authorizeUrl.split("\\?")[0];
        accessTokenUrl = accessTokenUrl.split("\\?")[0];
    }

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
