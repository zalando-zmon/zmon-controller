package org.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by elauria on 01/08/17.
 */
@Configuration
@EnableConfigurationProperties({GoogleanalyticsProperties.class})
public class GoogleanalyticsConfiguration {
}
