package com.travelinsurancemaster.services.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by Alex on 25.04.2016.
 */

@Component

public class CacheApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(CacheApplicationListener.class);

    private final CacheService cacheService;

    public CacheApplicationListener(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("CacheApplicationListener onContextRefreshedEvent");
        cacheService.fillPolicyMetaCache();
        cacheService.fillCategoryCache();
        log.debug("CacheApplicationListener onContextRefreshedEvent - cache prepared");
    }
}
