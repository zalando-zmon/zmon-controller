package org.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by elauria on 07/09/17.
 */
@Configuration
@EnableConfigurationProperties({InstanaProperties.class})
public class InstanaConfiguration {
}
