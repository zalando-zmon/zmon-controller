package de.zalando.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
public class RedisPoolConfiguration {

	@Autowired
	private RedisProperties redisProperties;
	
	// TODO, use this to fetch mission properties
	@Autowired
	private Environment environment;

	@Bean
	public JedisPool jedisPool() {
		return new JedisPool(jedisPoolConfig(), redisProperties.getHost(), redisProperties.getPort());
	}

//			frontend.redis.whenExhaustedAction = 1
//			frontend.redis.testOnBorrow = false
//			frontend.redis.testOnReturn = false
//			frontend.redis.testWhileIdle = true
//			frontend.redis.timeBetweenEvictionRunsMillis = 30000
//			frontend.redis.numTestsPerEvictionRun = -1
//			frontend.redis.minEvictableIdleTimeMillis = 60000
//			frontend.redis.softMinEvictableIdleTimeMillis = -1
	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		
		poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
		poolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
		poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
		
//		poolConfig.setMaxWaitMillis(maxWaitMillis);
		
		
//		poolConfig.setBlockWhenExhausted(blockWhenExhausted);
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
