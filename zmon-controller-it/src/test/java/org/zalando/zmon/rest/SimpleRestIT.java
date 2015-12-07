package org.zalando.zmon.rest;

import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author jbellmann
 *
 */
public class SimpleRestIT {

	@ClassRule
	public static SelfSignedCertsRule laxCertsRule = new SelfSignedCertsRule();

	/**
	 * Without any access-token this should fail with expected exception.
	 */
	@Test(expected = HttpClientErrorException.class)
	public void run() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForEntity("https://localhost:8443/api/v1/checks/all-active-alert-definitions", String.class);
	}
}
