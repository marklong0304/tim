package com.travelinsurancemaster.services.clients;

import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.sevencorners.*;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.util.RestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ritchie on 1/29/15.
 */
public class SevenCornersClient extends AbstractRestClient {

    public static final String ROUNDTRIP_ECONOMY_CODE = "14";
    public static final String ROUNDTRIP_ELITE_CODE = "1036";
    public static final String ROUNDTRIP_CHOICE_CODE = "1037";
    public static final String WANDER_FREQUENT_TRAVELER_CODE = "769";
    public static final String LIAISON_TRAVEL_ECONOMY_CODE = "1154";
    public static final String LIAISON_TRAVEL_CHOICE_CODE = "1155";
    public static final String LIAISON_TRAVEL_ELITE_CODE = "1156";
    public static final String LIAISON_INTERNATIONAL_CODE = "29";

    private static final Map<String, Set<String>> categoryCodeToQuoteFieldCodes = Stream.of(
            new AbstractMap.SimpleEntry<String, Set<String>>(CategoryCodes.RENTAL_CAR, Stream.of("RCarD_BND", "CDW_BND").collect(Collectors.toSet())),
            new AbstractMap.SimpleEntry<String, Set<String>>(CategoryCodes.FLIGHT_ONLY_AD_AND_D, Stream.of("FliAcc_BND").collect(Collectors.toSet())),
            new AbstractMap.SimpleEntry<String, Set<String>>(CategoryCodes.CANCEL_FOR_ANY_REASON, Stream.of("CFAR_BND").collect(Collectors.toSet())),
            new AbstractMap.SimpleEntry<String, Set<String>>(CategoryCodes.CANCEL_FOR_WORK_REASONS, Stream.of("CWR_BND").collect(Collectors.toSet())),
            new AbstractMap.SimpleEntry<String, Set<String>>(CategoryCodes.HAZARDOUS_SPORTS, Stream.of("HSC_BND").collect(Collectors.toSet())),
            new AbstractMap.SimpleEntry<String, Set<String>>(CategoryCodes.MEDICAL_DEDUCTIBLE, Stream.of("DedPgm_BND").collect(Collectors.toSet())),
            new AbstractMap.SimpleEntry<String, Set<String>>(CategoryCodes.EMERGENCY_MEDICAL, Stream.of("MMPgm_BND").collect(Collectors.toSet()))
        )
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    @Autowired
    private Mapper mapper;

    private JWT jwt;

    public SevenCornersClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {

        QuoteResult quoteResult = new QuoteResult();

        SCQuoteRequest scQuoteRequest = mapper.createQuoteRequest(quoteRequest, policyMeta, policyMetaCode);
        HttpEntity<SCQuoteRequest> requestEntity = new HttpEntity<>(scQuoteRequest, createAuthHeaders());
        ResponseEntity<SCQuoteResponse> response = restTemplate.exchange(apiProperties.getSevenCorners().getUrl() + "/quote", HttpMethod.POST, requestEntity, SCQuoteResponse.class);
        SCQuoteResponse quoteResponse = response.getBody();

        if(!RestUtil.isError(response.getStatusCode())) {
            quoteResult.setStatus(Result.Status.SUCCESS);
            if(quoteResponse.getTotal() != null && (quoteResponse.getStatus() == ValidationStatus.VALID || quoteResponse.getStatus() == ValidationStatus.WARNING)) {
                Product product = new Product(policyMeta, policyMetaCode, BigDecimal.valueOf(quoteResponse.getTotal()));
                product.setQuoteIdentifier(quoteResponse.getQuoteIdentifier());
                product.setQuoteVersion(quoteResponse.getQuoteVersion());
                if(quoteResponse.getPolicyQuoteResponses() != null && quoteResponse.getPolicyQuoteResponses().size() > 0) {
                    product.setPolicyId(quoteResponse.getPolicyQuoteResponses().get(0).getPolicyId());
                }
                quoteResult.products.add(product);
            }
        } else {
            quoteResult.setStatus(Result.Status.ERROR);
        }

        return quoteResult;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest purchaseRequest) {

        SCPurchaseRequest scPurchaseRequest = mapper.createPurchaseRequest(purchaseRequest, apiProperties.getSevenCorners());
        HttpEntity<SCPurchaseRequest> requestEntity = new HttpEntity<>(scPurchaseRequest, createAuthHeaders());
        ResponseEntity<SCPurchaseResponse> response = restTemplate.exchange(apiProperties.getSevenCorners().getUrl() + "/purchase", HttpMethod.POST, requestEntity, SCPurchaseResponse.class);

        PurchaseResponse purchaseResponse = new PurchaseResponse();
        if(!RestUtil.isError(response.getStatusCode())) {
            SCPurchaseResponse scPurchaseResponse = response.getBody();
            if(scPurchaseResponse.getFieldValidations() != null && scPurchaseResponse.getFieldValidations().size() > 0) {
                purchaseResponse.setStatus(Result.Status.ERROR);
                for(FieldValidation fieldValidation : scPurchaseResponse.getFieldValidations()) {
                    purchaseResponse.getErrors().add(new Result.Error(fieldValidation.getStatus().name(), fieldValidation.getFriendlyMessage()));
                }
            } else {
                purchaseResponse.setStatus(Result.Status.SUCCESS);
                if(scPurchaseResponse.getPolicyCertificates() != null && scPurchaseResponse.getPolicyCertificates().size() > 0) {
                    purchaseResponse.setPolicyNumber(scPurchaseResponse.getPolicyCertificates().get(0).getCertificateNumber());
                }
            }
        } else {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error(response.getStatusCode().toString(), response.getStatusCode().getReasonPhrase()));
        }

        return purchaseResponse;
    }


    @Override
    public String getVendorCode() {
        return ApiVendor.SevenCorners;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        List<Result.Error> errors = new ArrayList<>();
        switch (policyMetaCode.getCode()) {
            case LIAISON_INTERNATIONAL_CODE:
            case WANDER_FREQUENT_TRAVELER_CODE:
                // todo: family question
                if (request.getTravelers().size() > 1) {
                    errors.add(new Result.Error("-1", "Only one traveler allowed"));
                }
                return errors;
        }
        return Collections.emptyList();
    }

    @Override
    public void filterPolicyUpsaleCategories(List<PolicyMetaCategory> upsaleCategories, Product product, QuoteRequest quoteRequest) {

        if(product != null && product.getPolicyId() != null) {

            HttpEntity requestEntity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<FormDefinitionResponse> response = restTemplate.exchange(
                    apiProperties.getSevenCorners().getUrl() + "/products/policyformdefinition/" + product.getPolicyId(), HttpMethod.GET, requestEntity, FormDefinitionResponse.class);
            FormDefinitionResponse formDefinitionResponse = response.getBody();
            Set<String> quoteFieldCodes = formDefinitionResponse.getQuoteFieldDefinitions().stream().map(qfd -> qfd.getFieldCode()).collect(Collectors.toSet());

            if (!RestUtil.isError(response.getStatusCode())) {
                upsaleCategories.removeIf(upsaleCategory -> Collections.disjoint(quoteFieldCodes, categoryCodeToQuoteFieldCodes.get(upsaleCategory.getCategory().getCode())));
            }
        }
    }

    private HttpHeaders createAuthHeaders(){
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
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", apiProperties.getSevenCorners().getAuthClientId());
        map.add("grant_type", apiProperties.getSevenCorners().getAuthGrantType());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<AuthResponse> response = restTemplate.exchange(apiProperties.getSevenCorners().getAuthUrl(), HttpMethod.POST, request, AuthResponse.class);
        jwt = new JWT(response.getBody().getAccessToken(), response.getBody().getExpiresIn());
    }

    @Override
    public boolean auth() {
        checkJwt();
        return true;
    }
}