package org.zalando.zmon.config.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @author jbellmann
 *
 */
@ConfigurationProperties(prefix = "spring.redis")
public class DefaultRedisProperties extends RedisProperties {
}
