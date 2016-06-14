package org.zalando.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.zmon.security.jwt.JWTService;
import org.zalando.zmon.security.jwt.JWTServiceProperties;

@Configuration
@EnableConfigurationProperties({ JWTServiceProperties.class })
public class JWTServiceConfiguration {

    @Autowired
    private JWTServiceProperties jwtServiceProperties;

    @Bean
    public JWTService jwtService(){
        return new JWTService(jwtServiceProperties);
    }

}
