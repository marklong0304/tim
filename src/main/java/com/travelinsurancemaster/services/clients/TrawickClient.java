package com.travelinsurancemaster.services.clients;


import com.google.common.collect.ImmutableMap;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.trawick.TrawickMapper;
import com.travelinsurancemaster.model.webservice.trawick.TrawickOrder;
import com.travelinsurancemaster.services.AbstractRestClient;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by ritchie on 1/28/15.
 */
public class TrawickClient extends AbstractRestClient {

    /* Safe Travels International */
    public static final String SAFE_TRAVELS_INTERNATIONAL_PLAN_CODE = "19";
    public static final String SAFE_TRAVELS_INTERNATIONAL_COST_SAVER_PLAN_CODE = "30";
    /* Travel Plans Inbound to the US */
    public static final String SAFE_TRAVELS_USA_PLAN_CODE = "16";
    public static final String SAFE_TRAVELS_USA_COST_SAVER_PLAN_CODE = "80";
    public static final String SAFE_TRAVELS_USA_COMPREHENSIVE_PLAN_CODE = "81";
    public static final String SAFE_TRAVELS_FOR_VISITORS_TO_THE_USA = "79";
    /* Trip Protection Plans */
    public static final String SAFE_TRAVELS_USA_TRIP_CANCELLATION_PLAN_CODE = "28";
    public static final String SAFE_TRAVELS_MULTINATIONAL_TRIP_CANCELLATION_PLAN_CODE = "48";
    public static final String SAFE_TRAVELS_3_IN_1_PLAN_CODE = "65";
    public static final String SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE = "66";
    public static final String SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE = "67";
    public static final String SAFE_TRAVELS_VACANTIONER_PLAN_CODE = "72";
    public static final String SAFE_TRAVELS_SHENGEN_VISA = "82";
    public static final String SAFE_TRAVELS_OUTBOUND = "83";
    public static final String SAFE_TRAVELS_OUTBOUND_COST_SAVER = "84";

    public static final String SAFE_TRAVELS_FOR_VISITORS_TO_THE_USA_DIAMOND_PLAN_UNIQUE_CODE = "T79D";
    public static final String SAFE_TRAVELS_FOR_VISITORS_TO_THE_USA_DIAMOND_PLUS_PLAN_UNIQUE_CODE = "T79DP";

    public static final String SAFE_TRAVELS_INTERNATIONAL_PLAN_UNIQUE_CODE = "T19";

    public static final String SUCCESS_QUOTE_STATUS_CODE = "0";
    public static final String SUCCESS_ORDER_STATUS_CODE = "1";

    private static final Map<String, String> CERTIFICATE_MAP = ImmutableMap.<String, String>builder()
            .put(SAFE_TRAVELS_USA_PLAN_CODE, "https://pdf.trawickinternational.com/PDFService.asmx/RetrieveSampleCert?agentID=1&productID=16")
            .put(SAFE_TRAVELS_INTERNATIONAL_PLAN_CODE, "https://pdf.trawickinternational.com/PDFService.asmx/RetrieveSampleCert?agentID=1&productID=19")
            .put(SAFE_TRAVELS_INTERNATIONAL_COST_SAVER_PLAN_CODE, "https://pdf.trawickinternational.com/PDFService.asmx/RetrieveSampleCert?agentID=1&productID=30")
            .put(SAFE_TRAVELS_USA_COST_SAVER_PLAN_CODE, "https://pdf.trawickinternational.com/PDFService.asmx/RetrieveSampleCert?agentID=1&productID=80")
            .put(SAFE_TRAVELS_USA_COMPREHENSIVE_PLAN_CODE, "https://pdf.trawickinternational.com/PDFService.asmx/RetrieveSampleCert?agentID=1&productID=81")
            .put(SAFE_TRAVELS_FOR_VISITORS_TO_THE_USA, "https://trawickinternational.com/assets/certificates/79-Safe%20Travels%20for%20Visitors%20to%20the%20USA/801_Visitors/sampleCert.pdf")
            .put(SAFE_TRAVELS_USA_TRIP_CANCELLATION_PLAN_CODE, "https://pdf.trawickinternational.com/PDFService.asmx/RetrieveSampleCert?agentID=1&productID=28")
            .put(SAFE_TRAVELS_MULTINATIONAL_TRIP_CANCELLATION_PLAN_CODE, "https://pdf.trawickinternational.com/PDFService.asmx/RetrieveSampleCert?agentID=1&productID=48")
            .put(SAFE_TRAVELS_3_IN_1_PLAN_CODE, "https://www.trawickinternational.com/assets/templates/NW31${stateCode}.pdf")
            .put(SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE, "https://www.trawickinternational.com/assets/templates/NWSTDefault.pdf")
            .put(SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE, "https://www.trawickinternational.com/assets/templates/NWFC${stateCode}Pet.pdf")

//            .put(SAFE_TRAVELS_VACANTIONER_PLAN_CODE, "https://pdf.trawickinternational.com/pdfservice.asmx/RetrieveVacationerCertificateRider?state=${stateCode}&upgrade=true")
            .put(SAFE_TRAVELS_VACANTIONER_PLAN_CODE, "https://pdf.trawickinternational.com/pdfservice.asmx/RetrieveVacationerCertificateRider?state=${stateCode}&upgrade=false")

            .put(SAFE_TRAVELS_SHENGEN_VISA, "https://www.trawickinternational.com/assets/certificates/82-Safe%20Travels%20Schengen%20Visa/815_STSV-52018/sampleCert.pdf")
            .put(SAFE_TRAVELS_OUTBOUND, "https://pdf.trawickinternational.com/PDFService.asmx/RetrieveSampleCert?agentID=1&productID=83")
            .put(SAFE_TRAVELS_OUTBOUND_COST_SAVER, "https://pdf.trawickinternational.com/PDFService.asmx/RetrieveSampleCert?agentID=1&productID=84")
            .build();

    private static final Logger log = LoggerFactory.getLogger(TrawickClient.class);

    @Autowired
    private TrawickMapper trawickMapper;

    public TrawickClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public TrawickOrder quote(MultiValueMap<String, String> params) {
        return restTemplate.postForObject(apiProperties.getTrawick().getUrl(), params, TrawickOrder.class);
    }

    public TrawickOrder order(MultiValueMap<String, String> params) {
        return restTemplate.postForObject(apiProperties.getTrawick().getUrl(), params, TrawickOrder.class);
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        String policyCode = policyMetaCode.getCode();
        log.debug("Product code: {}, product name: {}", policyCode, policyMetaCode.getName());

        MultiValueMap<String, String> requestParams = trawickMapper.toMap(request, policyMetaCode, apiProperties.getTrawick().getUser(), policyMeta);
        TrawickOrder response;
        try {
            response = quote(requestParams);
        } catch (HttpServerErrorException e) {
            String msg = String.format("Failed to perform request with %s product code", policyCode);
            log.error(msg, e);
            QuoteResult quoteResult = new QuoteResult();
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.getErrors().add(new Result.Error("", msg));
            return quoteResult;
        }
        QuoteResult quoteResult = new QuoteResult();
        if (response.getTotalPrice().equals("9999999999.0")) {
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.getErrors().add(new Result.Error("-1", "Trip cost per person is incorrect"));
        } else if (response.getOrderStatusCode().equals(SUCCESS_QUOTE_STATUS_CODE)) {
            quoteResult.setStatus(Result.Status.SUCCESS);
            BigDecimal totalPrice = new BigDecimal(response.getTotalPrice());
            totalPrice = totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
            final String certificateUrl = CERTIFICATE_MAP.get(policyMetaCode.getCode());
            String stateCode = "";
            if (policyMetaCode.getCode().equals(SAFE_TRAVELS_3_IN_1_PLAN_CODE)){
                List<String> states = Arrays.asList("CO", "IN", "KS", "LA", "MA", "MN", "MO", "NH", "NY", "OK", "OR", "PA", "SD", "TN", "TX", "VA");
                stateCode = states.contains(request.getResidentState().name()) ? request.getResidentState().name() : "Default";
            } else if (policyMetaCode.getCode().equals(SAFE_TRAVELS_VACANTIONER_PLAN_CODE)){
                stateCode = request.getResidentState().name();
            } else if (policyMetaCode.getCode().equals(SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE)){
                List<String> states = Arrays.asList("CO", "FL", "IN", "KS", "LA", "MA", "MO", "NH", "NY", "OK", "OR", "PA", "SD", "TN", "TX", "WA");
                stateCode = states.contains(request.getResidentState().name()) ? request.getResidentState().name() : "Default";
            }
            Map<String, String> valuesMap = new HashMap<>();

            valuesMap.put("stateCode", stateCode);
            StrSubstitutor sub = new StrSubstitutor(valuesMap);
            Product product = new Product(policyMeta, policyMetaCode, totalPrice, request.getUpsaleValues(), sub.replace(certificateUrl));
            quoteResult.products.add(product);
        } else {
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.getErrors().add(new Result.Error(response.getOrderStatusCode(), response.getOrderStatusMessage()));
        }
        return quoteResult;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {
        MultiValueMap<String, String> purchaseParams = trawickMapper.toMap(request, apiProperties.getTrawick().getUser());
        TrawickOrder orderResponse = order(purchaseParams);
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        if (orderResponse.getOrderStatusCode().equals(SUCCESS_ORDER_STATUS_CODE)) {
            purchaseResponse.setStatus(Result.Status.SUCCESS);
            purchaseResponse.setPolicyNumber(orderResponse.getOrderNo());
        } else {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(
                    new Result.Error(orderResponse.getOrderStatusCode(), orderResponse.getOrderStatusMessage()));
        }
        return purchaseResponse;
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.Trawick;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        return Collections.emptyList();
    }

    private List<Result.Error> getError(String productName, String message) {
        return Collections.singletonList(new Result.Error("-1", "Policy Meta \"" + productName + "\" is skipped due to a wrong country conditions. " + message));
    }
}
