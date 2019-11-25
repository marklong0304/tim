package com.travelinsurancemaster;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.travelinsurancemaster.services.cache.CustomCacheKeyGenerator;
import com.travelinsurancemaster.services.cache.RestrictionValidateCacheKeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by Chernov Artur on 31.07.15.
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    public static final String PRODUCTS_CACHE = "products";
    public static final String POLICY_META_CACHE = "POLICY_META_CACHE";
    public static final String CATEGORIES_CACHE = "CATEGORIES_CACHE";
    public static final String RESTRICTION_VALIDATION_CACHE = "RESTRICTION_VALIDATION_CACHE";


    @Value("${cache.expireAfterAccess}")
    private long expireAfterAccess;

    @Value("${cache.policy.meta.expireAfterAccess}")
    private long policyMetaExpireAfterAccess;

    @Value("${cache.maximumSize}")
    private long maximumSize;

    @Bean
    @Override
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        CaffeineCache productsCache = new CaffeineCache(PRODUCTS_CACHE, Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccess, TimeUnit.MINUTES)
                .build());

        CaffeineCache policyMetasCache = new CaffeineCache(POLICY_META_CACHE, Caffeine.newBuilder()
                .expireAfterAccess(policyMetaExpireAfterAccess, TimeUnit.MINUTES)
                .build());

        CaffeineCache categoriesCache = new CaffeineCache(CATEGORIES_CACHE, Caffeine.newBuilder()
                .expireAfterAccess(policyMetaExpireAfterAccess, TimeUnit.MINUTES)
                .build());

        CaffeineCache restrictionValidationCache = new CaffeineCache(RESTRICTION_VALIDATION_CACHE, Caffeine.newBuilder()
                .expireAfterAccess(policyMetaExpireAfterAccess, TimeUnit.MINUTES)
                .build());

        cacheManager.setCaches(Arrays.asList(productsCache, policyMetasCache, categoriesCache, restrictionValidationCache));
        return cacheManager;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new CustomCacheKeyGenerator();
    }

    @Bean("restrictionValidateCacheKeyGenerator")
    public KeyGenerator restrictionValidateCacheKeyGenerator() {
        return new RestrictionValidateCacheKeyGenerator();
    }
}