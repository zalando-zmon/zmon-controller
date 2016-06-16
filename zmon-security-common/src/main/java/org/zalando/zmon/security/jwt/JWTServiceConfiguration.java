package org.zalando.zmon.security.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ JWTServiceProperties.class })
public class JWTServiceConfiguration {

    @Bean
    public JWTService jwtService(JWTServiceProperties jwtServiceProperties) {
        return new JWTService(jwtServiceProperties);
    }
}
