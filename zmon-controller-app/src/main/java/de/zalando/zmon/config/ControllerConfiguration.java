package de.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jmussler on 26.02.16.
 */
@Configuration
@EnableConfigurationProperties({ControllerProperties.class})
public class ControllerConfiguration {
}
