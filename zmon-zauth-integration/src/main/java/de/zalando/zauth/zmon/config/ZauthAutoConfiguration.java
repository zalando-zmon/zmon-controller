package de.zalando.zauth.zmon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@Import({SecurityConfig.class, SocialConfig.class})
public class ZauthAutoConfiguration {
	
	@Bean
	public SigninController signinController(){
		return new SigninController();
	}
}
