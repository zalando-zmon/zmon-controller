package de.zalando.zauth.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Only for this example. Not official part of the lib. Could be done this way.
 *
 * @author jbellmann
 */
@ConfigurationProperties(prefix = "zauth")
public class ZAuthProperties {

	private String clientId;

	private String clientSecret;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(final String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(final String clientSecret) {
		this.clientSecret = clientSecret;
	}

}