package org.zalando.github.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zalando.zmon.config.ZmonOAuth2Properties;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@EnableConfigurationProperties({ZmonOAuth2Properties.class})
@Import({ SecurityConfig.class, SocialConfig.class })
public class ZmonGithubAutoConfiguration {

	@Bean
	public SigninController signinController() {
		return new SigninController();
	}
}
