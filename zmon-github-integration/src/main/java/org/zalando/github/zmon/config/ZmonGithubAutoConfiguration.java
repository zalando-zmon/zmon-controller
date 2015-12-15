package org.zalando.github.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import org.zalando.github.zmon.security.GithubSignupConditionProperties;

import org.zalando.zmon.config.ZmonOAuth2Properties;
import org.zalando.zmon.security.SigninController;

/**
 * @author  jbellmann
 */
@Configuration
@EnableConfigurationProperties({ ZmonOAuth2Properties.class, GithubSignupConditionProperties.class })
@Import(
    {
        GithubSecurityConfig.class, GithubSocialConfigurer.class, GithubAccountConnectionSignupConfig.class,
        GithubSignupConditionsConfig.class
    }
)
@Profile("github")
public class ZmonGithubAutoConfiguration {

    @Bean
    public SigninController signinController() {
        return new SigninController();
    }
}
