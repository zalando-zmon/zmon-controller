package org.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hjacobs on 2/5/16.
 */
@Configuration
@EnableConfigurationProperties({NotificationServiceProperties.class})
public class NotificationServiceConfiguration {
}
