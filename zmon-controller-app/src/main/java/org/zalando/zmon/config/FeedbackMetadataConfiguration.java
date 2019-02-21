package org.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({FeedbackMetadataProperties.class})
public class FeedbackMetadataConfiguration {
}
