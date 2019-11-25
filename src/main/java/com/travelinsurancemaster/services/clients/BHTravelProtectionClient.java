package com.travelinsurancemaster.services.clients;

import com.google.common.base.Objects;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.bhtravelprotection.*;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.util.RestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by maleev on 21.04.2016.
 */
public class BHTravelProtectionClient extends AbstractRestClient {

    @Autowired
    private BHTPMapper bhtpMapper;

    private BhtpJWT jwt;

    public BHTravelProtectionClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {

        BHTPQuoteRequest bhtpQuoteRequest = bhtpMapper.createQuoteRequest(request, policyMeta, policyMetaCode.getCode(), apiProperties.getbHTravelProtection());
        HttpEntity<BHTPQuoteRequest> requestEntity = new HttpEntity<>(bhtpQuoteRequest, createAuthHeaders());

        ResponseEntity<BHTPQuoteResponse> response = restTemplate.exchange(apiProperties.getbHTravelProtection().getUrl(),
                HttpMethod.POST, requestEntity, BHTPQuoteResponse.class);

        QuoteResult quoteResult = new QuoteResult();
        BHTPQuoteResponse quoteResponse = response.getBody();
        //check if respose code is 4xx or 5xx
        if (RestUtil.isError(response.getStatusCode())){
            quoteResult.setStatus(Result.Status.ERROR);
            if (quoteResponse.getMessages().length > 0){
                for (BHTPQuoteResponse.BHTPErrorMessage message: quoteResponse.getMessages()){
                    quoteResult.getErrors().add(new Result.Error(message.getCode(), message.getText()));
                }
            }
        } else {//everything ok
            if (Objects.equal(policyMetaCode.getName(), quoteResponse.getPackageName()) && quoteResponse.getBaseQuoteAmount() > 0) {
                quoteResult.setStatus(Result.Status.SUCCESS);

                Product product = new Product(policyMeta, policyMetaCode, getProductTotalPrice(bhtpQuoteRequest, quoteResponse), request.getUpsaleValues());
                quoteResult.products.add(product);
            } else {
                quoteResult.setStatus(Result.Status.ERROR);
                quoteResult.getErrors().add(new Result.Error("-1", "Quote request is failed"));
            }
        }
        return quoteResult;
    }

    private BigDecimal getProductTotalPrice(BHTPQuoteRequest bhtpQuoteRequest, BHTPQuoteResponse quoteResponse) {
        double premium = quoteResponse.getBaseQuoteAmount() + quoteResponse.getFees();
        if (bhtpQuoteRequest.getCoverages().size() > 0) {
            premium += quoteResponse.getOptionalCoveragesQuoteAmount();
        }
        return BigDecimal.valueOf(premium);
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {

        BHTPQuoteRequest bhtpQuoteRequest = bhtpMapper.createPurchaseRequest(request, apiProperties.getbHTravelProtection());
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<BHTPQuoteRequest> requestEntity = new HttpEntity<>(bhtpQuoteRequest,headers);
        ResponseEntity<BHTPQuoteResponse> restResponse = restTemplate.exchange(apiProperties.getbHTravelProtection().getPurchaseUrl(), HttpMethod.POST, requestEntity, BHTPQuoteResponse.class);

        PurchaseResponse purchaseResponse = new PurchaseResponse();
        BHTPQuoteResponse bhtpQuoteResponse = restResponse.getBody();

        if (bhtpQuoteResponse.getMessages() == null || bhtpQuoteResponse.getMessages().length == 0) {
            purchaseResponse.setStatus(Result.Status.SUCCESS);
            purchaseResponse.setPolicyNumber(String.valueOf(bhtpQuoteResponse.getPolicyNumber()));
        } else {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error("-1", "Purchase request is failed, " + bhtpQuoteResponse.getMessages()[0].getText()));
        }

        return purchaseResponse;
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.BHTravelProtection;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        return null;
    }



    /**
     * Creates HTTP Headers with authorization token.
     * If token is empty then requests it first
     * @return http headers
     */
    private HttpHeaders createAuthHeaders(){
        checkJwt();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add("Authorization", "Bearer " + jwt.getJwt());
        return requestHeaders;
    }

    /**
     * For each request we need the fresh token
     */
    private void requestNewJWT() {
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(apiProperties.getbHTravelProtection().getLoginUrl(),
                bhtpMapper.createAuthRequest(apiProperties.getbHTravelProtection()), AuthResponse.class);

        jwt = new BhtpJWT(response.getBody().getToken());
    }

    private void checkJwt() {
        if (jwt == null || jwt.isExpired()) {
            requestNewJWT();
        }
    }

    @Override
    public boolean auth() {
        checkJwt();
        return true;
    }

    public QuoteResult testQuoteInternalMethod(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode){
        return this.quoteInternal(quoteRequest,policyMeta, policyMetaCode);
    }

    public PurchaseResponse testPurchaseInternal(PurchaseRequest purchaseRequest){
        return purchaseInternal(purchaseRequest);
    }


}
