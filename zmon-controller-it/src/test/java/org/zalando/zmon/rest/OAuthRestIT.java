package org.zalando.zmon.rest;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.stups.oauth2.spring.client.StupsOAuth2RestTemplate;
import org.zalando.stups.oauth2.spring.client.StupsTokensAccessTokenProvider;
import org.zalando.stups.tokens.AccessTokens;

import com.unknown.pkg.ExampleApplication;

/**
 * Runs an {@link ExampleApplication} like an real service.
 * 
 * @author jbellmann
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ExampleApplication.class})
@WebIntegrationTest(randomPort=true)
@ActiveProfiles("local")
@DirtiesContext
public class OAuthRestIT {
	
	@ClassRule
	public static SelfSignedCertsRule selfSignedCertsRule = new SelfSignedCertsRule();
	
	@Autowired
	private AccessTokens accessTokens;

	@Test
	public void run() {
		StupsOAuth2RestTemplate restTemplate = new StupsOAuth2RestTemplate(new StupsTokensAccessTokenProvider("zmon", accessTokens));
		ResponseEntity<String> response = restTemplate.getForEntity("https://localhost:8443/api/v1/checks/all-active-alert-definitions", String.class);
		System.out.println(response.getBody());
	}
}
