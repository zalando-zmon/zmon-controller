package org.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JaegerProperties.class})
public class JaegerConfiguration {
}
