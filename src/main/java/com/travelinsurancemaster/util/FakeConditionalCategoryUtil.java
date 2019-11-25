package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.webservice.common.QuoteRequest;

/**
 * Created by ritchie on 8/3/16.
 */
public class FakeConditionalCategoryUtil extends ConditionalCategoryUtil {

    public FakeConditionalCategoryUtil(QuoteRequest quoteRequest) {
        super(quoteRequest);
    }

    /**
     * @return true, because not need to evaluate groovy condition, just check it for runtime exceptions.
     */
    @Override
    public boolean has(String code, String type, String val) {
        return true;
    }
}
