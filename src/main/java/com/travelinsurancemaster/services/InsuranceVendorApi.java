package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;

import java.util.List;

/**
 * Created by Vlad on 28.02.2015.
 */
public interface InsuranceVendorApi {

    QuoteResult quoteEstimate(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode);

    QuoteResult quote(QuoteRequest request, PolicyMeta policyMeta);

    PurchaseResponse purchase(PurchaseRequest request);

    String getVendorCode();

    void filterPolicyUpsaleCategories(List<PolicyMetaCategory> upsaleCategories, Product product, QuoteRequest quoteRequest);

    List<Result.Error> validateQuote(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode);

    boolean auth();
}
