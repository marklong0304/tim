package com.travelinsurancemaster.services.clients;

import com.google.common.base.Objects;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.travelinsure.*;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.util.RestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class TravelInsureClient extends AbstractRestClient {

    @Autowired
    private TIMapper tiMapper;

    private TiJWT jwt;

    public TravelInsureClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public QuoteResult quoteEstimate(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        QuoteResult quoteResult = new QuoteResult();
        quoteResult.quoteRequest = quoteRequest;
        quoteResult.setStatus(Result.Status.SUCCESS);
        Product product = new Product(policyMeta, policyMetaCode, tiMapper.estimatePremium(quoteRequest, policyMeta, policyMetaCode, false));
        quoteResult.products.add(product);
        return quoteResult;
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {

        TIQuoteRequest tiQuoteRequest = tiMapper.createQuoteRequest(request, policyMeta, policyMetaCode, apiProperties.getTravelInsure());
        HttpEntity<TIQuoteRequest> requestEntity = new HttpEntity<>(tiQuoteRequest, createAuthHeaders());

        ResponseEntity<TIQuoteResponse> response = restTemplate.exchange(apiProperties.getTravelInsure().getUrl(),
                HttpMethod.POST, requestEntity, TIQuoteResponse.class);

        QuoteResult quoteResult = new QuoteResult();
        TIQuoteResponse quoteResponse = response.getBody();

        if (RestUtil.isError(response.getStatusCode())){
            quoteResult.setStatus(Result.Status.ERROR);
        } else {
            if(quoteResponse.getErrorNumber() == 0) {
                if (quoteResponse.getRaterResponse() != null && Objects.equal(policyMetaCode.getName(), quoteResponse.getRaterResponse().getRaterPlan().getPlanName()) && quoteResponse.getRaterResponse().getRaterPlan().getPremium() > 0) {
                    quoteResult.setStatus(Result.Status.SUCCESS);

                    Product product = new Product(policyMeta, policyMetaCode, getProductTotalPrice(tiQuoteRequest, quoteResponse), request.getUpsaleValues());
                    quoteResult.products.add(product);
                } else {
                    quoteResult.setStatus(Result.Status.ERROR);
                    quoteResult.getErrors().add(new Result.Error("-1", quoteResponse.getSettlementResponse().getMessage()));
                }
            } else {
                quoteResult.setStatus(Result.Status.ERROR);
                quoteResult.getErrors().add(new Result.Error("-1", quoteResponse.getResultMessage()));
            }
        }
        return quoteResult;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {

        TIQuoteRequest tiQuoteRequest = tiMapper.createPurchaseRequest(request, apiProperties.getTravelInsure());
        HttpEntity<TIQuoteRequest> requestEntity = new HttpEntity<>(tiQuoteRequest, createAuthHeaders());

        ResponseEntity<TIQuoteResponse> response = restTemplate.exchange(apiProperties.getTravelInsure().getUrl(),
                HttpMethod.POST, requestEntity, TIQuoteResponse.class);

        PurchaseResponse purchaseResponse = new PurchaseResponse();
        TIQuoteResponse tiQuoteResponse = response.getBody();

//        if (tiQuoteResponse.getMessages() == null || tiQuoteResponse.getMessages().length == 0) {
        if (response.getStatusCode().is2xxSuccessful() && tiQuoteResponse.getErrorNumber() < 1) {
            purchaseResponse.setStatus(Result.Status.SUCCESS);
//            purchaseResponse.setPolicyNumber(String.valueOf(tiQuoteResponse.getPolicyNumber()));
            purchaseResponse.setPolicyNumber(String.valueOf(tiQuoteResponse.getRaterResponse().getRaterPlan().getPlanName()));
        } else {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error("-1", tiQuoteResponse.getSettlementResponse().getMessage()));
        }

        return purchaseResponse;

    }

    @Override
    public String getVendorCode() {
        return ApiVendor.TravelInsure;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        return null;
    }

    private BigDecimal getProductTotalPrice(TIQuoteRequest bhtpQuoteRequest, TIQuoteResponse quoteResponse) {
        return new BigDecimal(quoteResponse.getRaterResponse().getRaterPlan().getPremium());
    }

    private HttpHeaders createAuthHeaders() {
        checkJwt();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add("Authorization", "Bearer " + jwt.getJwt());
        return requestHeaders;
    }

    private void checkJwt() {
        if (jwt == null || jwt.isExpired()) {
            requestNewJWT();
        }
    }

    private void requestNewJWT() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", apiProperties.getTravelInsure().getAuthClientId());
        headers.set("client_secret", apiProperties.getTravelInsure().getAuthClientSecret());
        headers.set("scope", apiProperties.getTravelInsure().getAuthScope());
        headers.set("grant_type", apiProperties.getTravelInsure().getAuthGrantType());

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String params = "?client_id"+ "=" + apiProperties.getTravelInsure().getAuthClientId();
        params += "&client_secret"+ "=" + apiProperties.getTravelInsure().getAuthClientSecret();
        params += "&scope"+ "=" + apiProperties.getTravelInsure().getAuthScope();
        params += "&grant_type"+ "=" + apiProperties.getTravelInsure().getAuthGrantType();

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<AuthResponse> response = restTemplate.exchange(
        apiProperties.getTravelInsure().getAuthUrl() + params, HttpMethod.POST, entity, AuthResponse.class);

        jwt = new TiJWT(response.getBody().getAccessToken(), response.getBody().getExpiresIn());
    }

    @Override
    public boolean auth() {
        checkJwt();
        return true;
    }
}