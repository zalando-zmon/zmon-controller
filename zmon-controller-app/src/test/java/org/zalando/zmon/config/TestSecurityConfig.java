package org.zalando.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.zalando.zmon.security.AuthorityService;
import org.zalando.zmon.security.ZmonResourceServerConfigurer;
import org.zalando.zmon.security.service.PresharedTokensResourceServerTokenServices;

@Configuration
@PropertySource("classpath:/test.properties")
@EnableWebSecurity
@EnableResourceServer
@Order(4)
public class TestSecurityConfig {

    @Autowired
    AuthorityService authorityService;

    @Autowired
    Environment environment;

    @Bean
    public ResourceServerConfigurer testResourceServerConfigurer() {
        return new ZmonResourceServerConfigurer(new PresharedTokensResourceServerTokenServices(authorityService, environment));

    }
}
