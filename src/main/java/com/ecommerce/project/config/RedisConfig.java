package com.ecommerce.project.config;

import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    private static final String PRODUCTS_CACHE = "PRODUCTS";
    private static final String CATEGORIES_CACHE = "CATEGORIES";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(
                PRODUCTS_CACHE,
                defaultCacheConfiguration.serializeValuesWith(serializationPair(ProductResponse.class))
        );
        cacheConfigurations.put(
                CATEGORIES_CACHE,
                defaultCacheConfiguration.serializeValuesWith(serializationPair(CategoryResponse.class))
        );

        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaultCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    private <T> RedisSerializationContext.SerializationPair<T> serializationPair(Class<T> clazz) {
        return RedisSerializationContext.SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(clazz));
    }
}
