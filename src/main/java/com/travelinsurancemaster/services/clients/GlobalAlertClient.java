package com.travelinsurancemaster.services.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.globalalert.GAPurchaseResponse;
import com.travelinsurancemaster.model.webservice.globalalert.GAQuoteResponse;
import com.travelinsurancemaster.model.webservice.globalalert.GlobalAlertMapper;
import com.travelinsurancemaster.services.AbstractRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by maleev on 30.05.2016.
 */
public class GlobalAlertClient extends AbstractRestClient {

    @Autowired
    GlobalAlertMapper globalAlertMapper;

    ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

    private static String NO_PRODUCTS_RESPONSE = "no_products";

    public GlobalAlertClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {

        HttpHeaders quoteHttpHeaders = createQuoteHttpHeaders();
        MultiValueMap<String, String> quoteParams = globalAlertMapper.RequestToMap(request,policyMeta, policyMetaCode.getCode(), apiProperties.getGlobalAlert());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(quoteParams, quoteHttpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(apiProperties.getGlobalAlert().getUrl(),
                HttpMethod.POST, requestEntity, String.class);
        return this.processQuoteResponse(response.getBody(), policyMeta, policyMetaCode, request);
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {
        HttpHeaders quoteHttpHeaders = createQuoteHttpHeaders();
        MultiValueMap<String, String> purchaseParams = globalAlertMapper.purchaseToMap(request, apiProperties.getGlobalAlert());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(purchaseParams, quoteHttpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(apiProperties.getGlobalAlert().getPurchaseUrl(),
                HttpMethod.POST, requestEntity, String.class);

        return processPurchaseResponse(response.getBody());
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.GlobalAlert;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        if (!request.getResidentCountry().equals(CountryCode.US) && !request.getResidentCountry().equals(CountryCode.CA)){
            return Arrays.asList(new Result.Error("-1","Resident country must be USA or Canada"));
        }

        if (request.getTravelers().size() > 9){
            return Arrays.asList(new Result.Error("-1","GlobalAlert vendor supports 9 travelers max"));
        }

        if (GlobalAlertMapper.restrictedCountries.contains(request.getDestinationCountry())){
            return Arrays.asList(new Result.Error("-1", "Destination country is in the list of destination restrictions for the Global Alert plans."));
        }
        return null;
    }

    private HttpHeaders createQuoteHttpHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

    private PurchaseResponse processPurchaseResponse(String purchaseResponse){

        PurchaseResponse timPurchaseResponse = new PurchaseResponse();
        GAPurchaseResponse gaPurchaseResponse = null;

        try{
            gaPurchaseResponse = mapper.readValue(purchaseResponse, GAPurchaseResponse.class);
        }catch (IOException ex){
            timPurchaseResponse.error("-1", "response parsing failed. Check response format.");
            return timPurchaseResponse;
        }

        if (gaPurchaseResponse.getErrors() != null && gaPurchaseResponse.getErrors().size() > 0){
            for(GAPurchaseResponse.Error error: gaPurchaseResponse.getErrors()){
                timPurchaseResponse.error(error.getErrorCode(),error.getErrorText());
            }
        } else {
            timPurchaseResponse.setStatus(Result.Status.SUCCESS);
            timPurchaseResponse.setPolicyNumber(gaPurchaseResponse.getEncEnrollmentId());
        }

        return timPurchaseResponse;
        
    }


    private QuoteResult processQuoteResponse(String response, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode, QuoteRequest request) {

        QuoteResult quoteResult = new QuoteResult();


        GAQuoteResponse gaQuoteResponse = null;
        try {
            gaQuoteResponse = mapper.readValue(response, GAQuoteResponse.class);
        } catch (IOException e) {
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.getErrors().add(new Result.Error("-1", "Quote request is failed. response parsing is failed. Probably vendor has changed response format"));
            return quoteResult;
        }

        if (gaQuoteResponse.getErrors() != null && gaQuoteResponse.getErrors().size() > 0){
            for(GAQuoteResponse.Error error: gaQuoteResponse.getErrors()){
                quoteResult.error(error.getErrorCode(),error.getErrorText());
            }
        } else {
            quoteResult.setStatus(Result.Status.SUCCESS);
            Product product = new Product(policyMeta, policyMetaCode, BigDecimal.valueOf(gaQuoteResponse.getTotalPremium()), request.getUpsaleValues());
            quoteResult.products.add(product);
        }

        return quoteResult;
    }

    public QuoteResult testQuoteInternalMethod(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode){
        return this.quoteInternal(quoteRequest,policyMeta, policyMetaCode);
    }

    public PurchaseResponse testPurchaseInternal(PurchaseRequest purchaseRequest){
        return purchaseInternal(purchaseRequest);
    }
}
