package com.travelinsurancemaster.services.clients;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.tripmate.*;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.services.CategoryCacheService;
import com.travelinsurancemaster.services.PolicyMetaCategoryValueService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.util.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by ritchie on 8/6/15.
 */
public abstract class TripMateClient extends AbstractRestClient {

    private static final Logger log = LoggerFactory.getLogger(TripMateClient.class);

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private CategoryCacheService categoryCacheService;

    @Autowired
    private TripMateMapper tripMateMapper;

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    private Map<String, Integer> flightOnlyAdAndDMap;

    public TripMateClient(RestTemplate restTemplate) {
        super(restTemplate);
        initFlightOnlyAdAndDMap();
    }

    private void initFlightOnlyAdAndDMap() {
        flightOnlyAdAndDMap = new HashMap<>();
        flightOnlyAdAndDMap.put("100000", 10);
        flightOnlyAdAndDMap.put("250000", 25);
        flightOnlyAdAndDMap.put("500000", 50);
    }

    public TripMateQuoteResponse quote(MultiValueMap<String, String> quoteParams, String quoteUrl) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(quoteParams, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(quoteUrl, HttpMethod.POST, requestEntity, String.class);
        TripMateQuoteResponse quoteResponse = JsonUtils.getObject(response.getBody(), TripMateQuoteResponse.class);
        return quoteResponse;
    }

    public TripMateBookResponse purchase(MultiValueMap<String, String> purchaseParams, String purchaseUrl) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(purchaseParams, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(purchaseUrl, HttpMethod.POST, requestEntity, String.class);
        String responseStr = getStringWithoutGarbage(response.getBody());
        TripMateBookResponse purchaseResponse = JsonUtils.getObject(responseStr, TripMateBookResponse.class);
        return purchaseResponse;
    }

    private String getStringWithoutGarbage(String body) {
        String responseStr = body.substring(body.indexOf("{"));
        if (responseStr.contains(",[]")) {
            responseStr = responseStr.replace(",[]", "");
        }
        return responseStr;
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        String policyCode = policyMetaCode.getCode();
        log.debug("Product code: {}, product name: {}", policyCode, policyMetaCode.getName());
        TripMateQuoteResponse quoteResponse = null;
        MultiValueMap<String, String> quoteParams = tripMateMapper.toMap(request, policyCode, apiProperties);
        switch (policyCode) {
            case MHRossClient.ASSET_PRODUCT_CODE:
            case MHRossClient.ASSET_PLUS_PRODUCT_CODE:
            case MHRossClient.BRIDGE_PRODUCT_CODE:
            case MHRossClient.COMPLETE_PRODUCT_CODE:
                quoteResponse = quote(quoteParams, apiProperties.getmHRoss().getUrl());
        }
        QuoteResult quoteResult = new QuoteResult();
        Map<String, Category> categoryMap = categoryCacheService.getCategoryMap();
        for (TripMatePlan plan : quoteResponse.plans) {
            for (TripMatePlanResponse planResponse : plan.plansByCode) {
                // calling per product
                if (!policyCode.equals(planResponse.code)) {
                    continue;
                }
                if (policyMetaService.isPolicyContainsCategory(policyMeta, request)) {
                    List<Result.Error> errors = validateQuote(request, policyMeta, policyMetaCode);
                    if (CollectionUtils.isNotEmpty(errors)) {
                        String s = StringUtils.join(errors.toArray(new Object[errors.size()]), "; ");
                        log.warn("Invalid params for quote request {}-{}: {}", getVendorCode(), policyCode, s);
                        continue;
                    }
                    String error = plan.plansByCode.get(0).error;
                    if (!StringUtils.isEmpty(error)) {
                        quoteResult.setStatus(Result.Status.ERROR);
                        quoteResult.getErrors().add(new Result.Error("-1", error));
                    } else {
                        BigDecimal totalCost = getUpsaleTotalCost(request, planResponse.total_cost);
                        Product product = new Product(policyMeta, policyMetaCode, totalCost, request.getUpsaleValues());
                        quoteResult.setStatus(Result.Status.SUCCESS);
                        quoteResult.products.add(product);
                    }
                }
            }
        }
        return quoteResult;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        MultiValueMap<String, String> purchaseParams = tripMateMapper.toMap(request, apiProperties);
        TripMateBookResponse bookResponse = null;
        switch (request.getProduct().getPolicyMetaCode().getCode()) {
            case MHRossClient.ASSET_PRODUCT_CODE:
            case MHRossClient.ASSET_PLUS_PRODUCT_CODE:
            case MHRossClient.BRIDGE_PRODUCT_CODE:
            case MHRossClient.COMPLETE_PRODUCT_CODE:
                bookResponse = purchase(purchaseParams, apiProperties.getmHRoss().getPurchaseUrl());
        }
        if (CollectionUtils.isEmpty(bookResponse.getErrors())) {
            purchaseResponse.setPolicyNumber(bookResponse.getPaymentId());
            purchaseResponse.setStatus(Result.Status.SUCCESS);
        } else {
            purchaseResponse.setStatus(Result.Status.ERROR);
            for (TripMateError error : bookResponse.getErrors()) {
                purchaseResponse.getErrors().add(new Result.Error(error.getErrorCode(), error.getErrorText()));
            }
        }
        return purchaseResponse;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        return Collections.emptyList();
    }

    private BigDecimal getUpsaleTotalCost(QuoteRequest request, BigDecimal total_cost) {
        String flightAdAndDValue = request.getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "");
        if (!flightAdAndDValue.isEmpty()) {
            total_cost = total_cost.add(BigDecimal.valueOf(flightOnlyAdAndDMap.get(flightAdAndDValue))
                    .multiply(BigDecimal.valueOf(request.getTravelers().size())));
        }
        String rentalCarValue = request.getUpsaleValue(CategoryCodes.RENTAL_CAR, "");
        if (!rentalCarValue.isEmpty()) {
            total_cost = total_cost.add(BigDecimal.valueOf(request.getRentalCarLength()).multiply(BigDecimal.valueOf(7)));
        }
        String hazardousSportValue = request.getUpsaleValue(CategoryCodes.HAZARDOUS_SPORTS, "");
        if (!hazardousSportValue.isEmpty()) {
            total_cost = total_cost.add(BigDecimal.valueOf(25).multiply(BigDecimal.valueOf(request.getTravelers().size())));
        }
        /*
        String personalProperty = request.getUpsaleValue(CategoryCodes.PERSONAL_PROPERTY, "");
        if (!personalProperty.isEmpty()){
            total_cost = total_cost.add(BigDecimal.valueOf(15).multiply(BigDecimal.valueOf(request.getTravelers().size())));
        }
        */
        return total_cost;
    }

    public static String getTripType(Set<String> tripTypes) {
        if (tripTypes.size() == 1) {
            if (tripTypes.contains(TripType.Air.name())) {
                return TripMateRequestConstants.TRIP_TYPE_AIR;
            }
            if (tripTypes.contains(TripType.Tour.name())) {
                return TripMateRequestConstants.TRIP_TYPE_LAND;
            }
            if (tripTypes.contains(TripType.Cruise.name())) {
                return TripMateRequestConstants.TRIP_TYPE_CRUISE;
            }
        }
        if (tripTypes.size() == 2) {
            if (tripTypes.contains(TripType.Air.name()) && tripTypes.contains(TripType.Tour.name())) {
                return TripMateRequestConstants.TRIP_TYPE_AIR_AND_LAND;
            }
            if (tripTypes.contains(TripType.Air.name()) && tripTypes.contains(TripType.Cruise.name())) {
                return TripMateRequestConstants.TRIP_TYPE_AIR_AND_CRUISE;
            }
        }
        if (tripTypes.size() == 3 && tripTypes.contains(TripType.Air.name()) &&
                tripTypes.contains(TripType.Tour.name()) && tripTypes.contains(TripType.Cruise.name())) {
            return TripMateRequestConstants.TRIP_TYPE_AIR_AND_LAND_AND_CRUISE;
        }
        return TripMateRequestConstants.TRIP_TYPE_OTHER;
    }
}