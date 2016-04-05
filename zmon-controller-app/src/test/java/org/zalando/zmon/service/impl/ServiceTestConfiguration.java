package org.zalando.zmon.service.impl;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.zalando.zmon.config.DataSourceProviderConfiguration;
import org.zalando.zmon.config.EventLogProperties;
import org.zalando.zmon.config.RedisPoolConfiguration;
import org.zalando.zmon.config.SchedulerProperties;
import org.zalando.zmon.config.XmlConfigFileConfiguration;
import org.zalando.zmon.persistence.ZMonSProcServiceConfig;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.impl.NoOpEventLog;
import org.zalando.zmon.service.impl.ZMonServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties({ RedisProperties.class, SchedulerProperties.class, EventLogProperties.class })
@Import({ DataSourceProviderConfiguration.class, ZMonSProcServiceConfig.class, XmlConfigFileConfiguration.class,
        RedisPoolConfiguration.class })
@ComponentScan(basePackageClasses = { ZMonServiceImpl.class })
@PropertySource("classpath:/test.properties")
public class ServiceTestConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public DefaultZMonPermissionService defaultZmonPermissionService() {
        return new DefaultZMonPermissionService();
    }

    @Bean
    public NoOpEventLog noOpEventLog() {
        return new NoOpEventLog();
    }

}
