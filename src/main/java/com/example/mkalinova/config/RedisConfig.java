package com.example.mkalinova.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;


@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {
	private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);
	
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		
		try {
			connectionFactory.getConnection().ping();
			log.info("Redis reachable — using RedisCacheManager");
			RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
					.entryTtl(Duration.ofHours(12))
					.disableCachingNullValues()
					.serializeValuesWith(
							RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
					);
			
			return RedisCacheManager.builder(connectionFactory)
					.cacheDefaults(defaultCacheConfig)
					.transactionAware()
					.build();
		} catch (Exception ex) {
			log.warn("Redis not reachable on startup, using SimpleCacheManager as fallback", ex);
			return new ConcurrentMapCacheManager();
		}
	}
}

