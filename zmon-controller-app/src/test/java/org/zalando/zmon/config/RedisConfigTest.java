package org.zalando.zmon.config;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.zmon.config.annotation.RedisWrite;
import org.zalando.zmon.config.redis.DefaultRedisProperties;
import org.zalando.zmon.config.redis.WriteRedisProperties;

import redis.clients.jedis.JedisPool;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RedisConfigTest {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    @RedisWrite
    private JedisPool writeJedisPool;

    @Test
    public void testPools() {
        Assertions.assertThat(jedisPool).isNotEqualTo(writeJedisPool);
    }

    @Configuration
    @Import({ RedisPoolConfiguration.class })
    static class TestConfig {

        @Bean
        public DefaultRedisProperties redisProperties() {
            DefaultRedisProperties properties = new DefaultRedisProperties();
            properties.setPool(new RedisProperties.Pool());

            return properties;
        }

        @Bean
        public WriteRedisProperties writeRedisProperties() {
            WriteRedisProperties properties = new WriteRedisProperties();
            properties.setPool(new WriteRedisProperties.Pool());
            return properties;
        }
    }
}
