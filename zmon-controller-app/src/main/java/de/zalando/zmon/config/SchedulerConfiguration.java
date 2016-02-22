package de.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hjacobs on 2/5/16.
 */
@Configuration
@EnableConfigurationProperties({SchedulerProperties.class})
public class SchedulerConfiguration {
}
