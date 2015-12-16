package de.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * Created by hjacobs on 16.12.15.
 */
@Configuration

@ComponentScan(basePackages = "de.zalando.zmon")
public class TestConfiguration {
}
