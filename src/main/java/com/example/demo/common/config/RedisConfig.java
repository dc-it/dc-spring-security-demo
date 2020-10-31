package com.example.demo.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * redis配置
 *
 * @author duchao
 */
@Configuration
@EnableCaching
public class RedisConfig {


    /**
     * 缓存管理器
     *
     * @param connectionFactory 连接工厂
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManagerBuilder cacheManagerBuilder = RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory);

        //默认缓存配置(等价于配置文件中设置spring.cache.redis.time-to-live)
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();
        cacheManagerBuilder.cacheDefaults(defaultCacheConfig);

        //其他缓存配置
        //cacheManagerBuilder.withInitialCacheConfigurations();

        return cacheManagerBuilder.build();
    }
}
