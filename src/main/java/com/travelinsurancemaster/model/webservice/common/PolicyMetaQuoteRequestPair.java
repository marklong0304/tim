package com.travelinsurancemaster.model.webservice.common;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Chernov Artur on 03.09.15.
 */
public class PolicyMetaQuoteRequestPair {
    private PolicyMeta policyMeta;
    private QuoteRequest quoteRequest;

    public PolicyMetaQuoteRequestPair(PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        this.policyMeta = policyMeta;
        this.quoteRequest = quoteRequest;
    }

    public PolicyMeta getPolicyMeta() {
        return policyMeta;
    }

    public void setPolicyMeta(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
    }

    public QuoteRequest getQuoteRequest() {
        return quoteRequest;
    }

    public void setQuoteRequest(QuoteRequest quoteRequest) {
        this.quoteRequest = quoteRequest;
    }

    @Override
    public String toString() {
        return "PolicyMetaQuoteRequestPair{" +
                "policyMeta=" + policyMeta +
                ", quoteRequest=" + quoteRequest +
                '}';
    }
}