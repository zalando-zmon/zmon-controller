package org.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author raparida
 */
@Configuration
@EnableConfigurationProperties({MetaDataProperties.class})
public class MetaDataConfiguration {
}
