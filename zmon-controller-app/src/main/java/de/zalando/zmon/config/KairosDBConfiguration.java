package de.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hjacobs on 1/28/16.
 */
@Configuration
@EnableConfigurationProperties({KairosDBProperties.class})
public class KairosDBConfiguration {
}
