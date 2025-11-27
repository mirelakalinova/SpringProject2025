package com.example.mkalinova.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CacheErrorConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheErrorConfig.class);

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("CACHE GET failed → cache='{}', key='{}'. Falling back to DB.",
                        cache != null ? cache.getName() : "null", key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("CACHE PUT failed → cache='{}', key='{}'. Error: {}",
                        cache != null ? cache.getName() : "null", key, e.toString());
                log.debug("PUT stacktrace:", e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("CACHE EVICT failed → cache='{}', key='{}'. Error: {}",
                        cache != null ? cache.getName() : "null", key, e.toString());
                log.debug("EVICT stacktrace:", e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("CACHE CLEAR failed → cache='{}'. Error: {}",
                        cache != null ? cache.getName() : "null", e.toString());
                log.debug("CLEAR stacktrace:", e);
            }
        };
    }
}
