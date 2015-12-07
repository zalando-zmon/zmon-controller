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

}
