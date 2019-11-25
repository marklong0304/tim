package com.travelinsurancemaster.services.clients;

import com.google.common.base.Objects;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.usaassist.UsaAssistMapper;
import com.travelinsurancemaster.model.webservice.usaassist.UsaAssistPurchaseResponse;
import com.travelinsurancemaster.model.webservice.usaassist.UsaAssistQuoteResponse;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ritchie on 10/1/15.
 */
public class UsaAssistClient extends AbstractRestClient {

    private static final Logger log = LoggerFactory.getLogger(UsaAssistClient.class);
    private static final String AGE = "age";

    public static final String TRAVEL_MEDICAL_STANDARD_CODE = "STD";
    public static final String TRAVEL_MEDICAL_STANDARD_UNIQUE_CODE = "UASTD2";

    public UsaAssistClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public UsaAssistQuoteResponse quote(MultiValueMap<String, String> quoteParams) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(quoteParams, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(apiProperties.getUsaAssist().getUrl(),
                HttpMethod.POST, requestEntity, String.class);
        UsaAssistQuoteResponse[] usaAssistQuoteResponse = JsonUtils.getObject(response.getBody(), UsaAssistQuoteResponse[].class);
        return usaAssistQuoteResponse[0];
    }

    public UsaAssistPurchaseResponse purchase(MultiValueMap<String, String> purchaseParams) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(purchaseParams, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(apiProperties.getUsaAssist().getPurchaseUrl(),
                HttpMethod.POST, requestEntity, String.class);
        UsaAssistPurchaseResponse purchaseResponse = JsonUtils.getObject(response.getBody(), UsaAssistPurchaseResponse.class);
        return purchaseResponse;
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        String policyCode = policyMetaCode.getCode();
        log.debug("Product code: {}, product name: {}", policyCode, policyMetaCode.getName());
        QuoteResult quoteResult = new QuoteResult();
        MultiValueMap<String, String> quoteParams = UsaAssistMapper.toMap(quoteRequest, policyCode, apiProperties.getUsaAssist());
        UsaAssistQuoteResponse quoteResponse = quote(quoteParams);
        if (Objects.equal(policyCode, quoteResponse.getPlanCode()) && quoteResponse.getTotal() != null && quoteResponse.getTotal().compareTo(BigDecimal.ZERO) > 0) {
            quoteResult.setStatus(Result.Status.SUCCESS);
            Product product = new Product(policyMeta, policyMetaCode, quoteResponse.getTotal(), quoteRequest.getUpsaleValues());
            quoteResult.products.add(product);
        } else {
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.getErrors().add(new Result.Error("-1", "Quote request is failed"));
        }
        return quoteResult;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest purchaseRequest) {
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        MultiValueMap<String, String> purchaseParams = UsaAssistMapper.toMap(purchaseRequest, apiProperties.getUsaAssist());
        UsaAssistPurchaseResponse response = purchase(purchaseParams);
        if (response.getError() == null) {
            purchaseResponse.setStatus(Result.Status.SUCCESS);
            purchaseResponse.setPolicyNumber(response.getCertificate());
        } else {
            purchaseResponse.setStatus(Result.Status.ERROR);
            UsaAssistPurchaseResponse.ErrorObject error = response.getError();
            if (error.getKey().equals(AGE)) {
                purchaseResponse.getErrors().add(new Result.Error("-1", "Traveler " + error.getTraveler() +
                        " age limit is exceeded: " + error.getValue() + " years"));
            } else {
                purchaseResponse.getErrors().add(new Result.Error("-1", error.getValue()));
            }
        }
        return purchaseResponse;
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.UsaAssist;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        if (request.getResidentCountry() == request.getDestinationCountry()) {
            return Arrays.asList(new Result.Error("-1", "Available only for traveling outside of country of residence."));
        }
        return Collections.emptyList();
    }
}
