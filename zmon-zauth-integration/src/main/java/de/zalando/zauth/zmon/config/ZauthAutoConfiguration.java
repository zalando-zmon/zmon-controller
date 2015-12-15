package de.zalando.zauth.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import org.zalando.zmon.config.ZmonOAuth2Properties;
import org.zalando.zmon.security.SigninController;

/**
 * @author  jbellmann
 */
@Configuration
@EnableConfigurationProperties({ ZmonOAuth2Properties.class })
@Import({ ZauthSecurityConfig.class, ZauthSocialConfigurer.class })
@Profile("zauth")
public class ZauthAutoConfiguration {

    @Bean
    public SigninController signinController() {
        return new SigninController();
    }
}
