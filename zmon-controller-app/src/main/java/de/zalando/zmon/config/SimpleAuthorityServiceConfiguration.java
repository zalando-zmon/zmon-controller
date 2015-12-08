package de.zalando.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.zalando.zmon.security.AuthorityService;
import de.zalando.zmon.security.simple.SimpleAuthorityService;
import de.zalando.zmon.security.simple.SimpleZmonAuthoritiesProperties;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@ConditionalOnMissingBean({AuthorityService.class})
@EnableConfigurationProperties({SimpleZmonAuthoritiesProperties.class})
public class SimpleAuthorityServiceConfiguration {
	
	@Autowired
	private SimpleZmonAuthoritiesProperties authProperties;
	
	@Bean
	public AuthorityService simpleAuthorityService(){
		return new SimpleAuthorityService(authProperties);
	}

}
