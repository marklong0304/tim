package com.travelinsurancemaster.services.clients;

import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.tripmate.TripMateBookResponse;
import com.travelinsurancemaster.model.webservice.tripmate.TripMateQuoteResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by ritchie on 8/4/15.
 */
public class MHRossClient extends TripMateClient {

    public static final String RCONSUMER = "rconsumer";

    public static final String ASSET = "Asset";
    public static final String ASSET_PLUS = "Asset  Plus";
    public static final String BRIDGE = "Bridge";
    public static final String COMPLETE = "Complete";
    public static final String ASSET_PRODUCT_CODE = "5316";
    public static final String ASSET_PLUS_PRODUCT_CODE = "5317";
    public static final String BRIDGE_PRODUCT_CODE = "5318";
    public static final String COMPLETE_PRODUCT_CODE = "5319";

    public MHRossClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public TripMateQuoteResponse quote(MultiValueMap<String, String> quoteParams) {
        return quote(quoteParams, apiProperties.getmHRoss().getUrl());
    }

    public TripMateBookResponse purchase(MultiValueMap<String, String> purchaseParams) {
        return purchase(purchaseParams, apiProperties.getmHRoss().getPurchaseUrl());
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        return super.quoteInternal(request, policyMeta, policyMetaCode);
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {
        if (request.getCreditCard().getCcCountry() != CountryCode.US) {
            PurchaseResponse purchaseResponse = new PurchaseResponse();
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error("-1", "Purchase error. Credit cards are only available for USA!"));
            return purchaseResponse;
        }
        return super.purchaseInternal(request);
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.MHRoss;
    }
}
