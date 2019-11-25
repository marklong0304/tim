package com.travelinsurancemaster.services.cache;

import com.travelinsurancemaster.model.cache.ProductCacheKey;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * Created by Chernov Artur on 31.07.15.
 */
public class CustomCacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params.length == 2) {
            return new ProductCacheKey((QuoteRequest) params[0], (PolicyMeta) params[1]);
        } else {
            return null;
        }
    }
}
