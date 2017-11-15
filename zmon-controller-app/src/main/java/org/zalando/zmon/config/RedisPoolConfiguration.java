package org.zalando.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.zalando.zmon.config.annotation.RedisWrite;
import org.zalando.zmon.config.redis.WriteRedisProperties;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@EnableConfigurationProperties({ WriteRedisProperties.class })
public class RedisPoolConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    @Autowired
    private WriteRedisProperties writeRedisProperties;

    // TODO, use this to fetch mission properties
    @Autowired
    private Environment environment;

    @Bean
    @Primary
    public JedisPool jedisPool() {
        return new JedisPool(jedisPoolConfig(),
                redisProperties.getHost(),
                redisProperties.getPort(),
                redisProperties.getTimeout());
    }

    // frontend.redis.whenExhaustedAction = 1
    // frontend.redis.testOnBorrow = false
    // frontend.redis.testOnReturn = false
    // frontend.redis.testWhileIdle = true
    // frontend.redis.timeBetweenEvictionRunsMillis = 30000
    // frontend.redis.numTestsPerEvictionRun = -1
    // frontend.redis.minEvictableIdleTimeMillis = 60000
    // frontend.redis.softMinEvictableIdleTimeMillis = -1
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        if (redisProperties.getPool() == null) {
            redisProperties.setPool(new RedisProperties.Pool());
        }

        poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
        poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());

        poolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait());

        // poolConfig.setBlockWhenExhausted(blockWhenExhausted);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        poolConfig.setNumTestsPerEvictionRun(-1);
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        poolConfig.setSoftMinEvictableIdleTimeMillis(-1);

        //
        return poolConfig;
    }

    @Bean
    @RedisWrite
    public JedisPool writeJedisPool() {
        return new JedisPool(writeJedisPoolConfig(),
                writeRedisProperties.getHost(),
                writeRedisProperties.getPort(),
                writeRedisProperties.getTimeout());
    }

    @Bean
    public JedisPoolConfig writeJedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        if (writeRedisProperties.getPool() == null) {
//            writeRedisProperties.setPool(new RedisProperties.Pool());
//        }

        poolConfig.setMaxIdle(writeRedisProperties.getPool().getMaxIdle());
        poolConfig.setMinIdle(writeRedisProperties.getPool().getMinIdle());
        poolConfig.setMaxTotal(writeRedisProperties.getPool().getMaxActive());

        // poolConfig.setMaxWaitMillis(maxWaitMillis);

        // poolConfig.setBlockWhenExhausted(blockWhenExhausted);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        poolConfig.setNumTestsPerEvictionRun(-1);
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        poolConfig.setSoftMinEvictableIdleTimeMillis(-1);

        //
        return poolConfig;
    }
}
