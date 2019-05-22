package org.zalando.zmon.security.grafanatoken;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.zmon.security.jwt.JWTServiceProperties;

@Configuration
@EnableConfigurationProperties({ JWTServiceProperties.class })
public class GrafanaTokenServiceConfiguration {

    @Bean
    public GrafanaTokenService grafanaTokenService(JWTServiceProperties jwtServiceProperties) {
        return new GrafanaTokenService(jwtServiceProperties);
    }
}
