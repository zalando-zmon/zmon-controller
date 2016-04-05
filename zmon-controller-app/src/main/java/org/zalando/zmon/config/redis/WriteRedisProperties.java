package org.zalando.zmon.config.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * We need a write Redis-Connections.
 * 
 * @author jbellmann
 *
 */
@ConfigurationProperties(prefix = "zmon.redis.write")
public class WriteRedisProperties extends RedisProperties {
}
