package org.zalando.zmon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by elauria on 17/05/17.
 */
@Configuration
@EnableConfigurationProperties({ConsentProperties.class})
public class ConsentConfiguration {
}
