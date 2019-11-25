package com.travelinsurancemaster.services.cache;

import com.travelinsurancemaster.model.cache.CalculatedRestrictionValidateCacheKey;
import com.travelinsurancemaster.model.cache.RestrictionValidateCacheKey;
import com.travelinsurancemaster.model.dto.Restriction;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

public class RestrictionValidateCacheKeyGenerator<T extends Restriction> implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params.length == 3) {
            return new CalculatedRestrictionValidateCacheKey((QuoteRequest) params[0], (String) params[1], (Boolean) params[2]);
        } else if (params.length == 2) {
            return new RestrictionValidateCacheKey((QuoteRequest) params[0], (T) params[1]);
        } else {
            return null;
        }
    }
}
