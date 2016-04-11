package org.zalando.zmon.config.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.zalando.zmon.config.annotation.RedisWrite;

/**
 * We need a write Redis-Connections.
 * 
 * @author jbellmann
 *
 */
@ConfigurationProperties(prefix = "zmon.redis.write")
@RedisWrite
public class WriteRedisProperties extends RedisProperties {
}
