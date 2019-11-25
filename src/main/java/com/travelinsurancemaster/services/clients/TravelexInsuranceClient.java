package com.travelinsurancemaster.services.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelinsurancemaster.clients.travelex.GenerateKeyResponse;
import com.travelinsurancemaster.clients.travelex.GetOnusTokenResponse;
import com.travelinsurancemaster.clients.travelex.InsuranceBookRS;
import com.travelinsurancemaster.clients.travelex.PaymentConfigurationResponse;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.travelexinsurance.*;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.util.JsonUtils;
import com.tsys.transit.tsep.TransITCrypt;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ritchie on 4/25/15.
 */
public class TravelexInsuranceClient extends AbstractRestClient {

    @Autowired
    private TravelexInsuranceMapper travelexInsuranceMapper;

    private static final Logger log = LoggerFactory.getLogger(TravelexInsuranceClient.class);
    public static final String TRAVEL_BASIC_PRODUCT_CODE = "STB";
    public static final String TRAVEL_SELECT_PRODUCT_CODE = "STS";
    public static final String BUSINESS_TRAVELER_SINGLE_CODE = "SBS";
    public static final String BUSINESS_TRAVELER_MULTI_CODE = "SBM";
    public static final String FLIGHT_INSURE_PRODUCT_CODE = "FIB";
    public static final String FLIGHT_INSURE_PLUS_PRODUCT_CODE = "FIPB";

    public static final String MERCHANT_ID = "887000003223";
    public static final String DEVICE_ID = "88700000322302";
    public static final String DEVELOPER_ID = "003223G001";
    public static final String USER_ID = "TA5618176";
    public static final String PASSWORD = "Travel123!";
    public static final String ENDPOINT_URL = "https://stagegw.transnox.com/servlets/TransNox_API_Server";
    public static final String USER_AGENT = "infonox";

    private static final int GENERATE_KEY_RESPONSE_OFFSET = 21;

    public TravelexInsuranceClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public InsuranceBookRS quote(MultiValueMap<String, String> params) {
        InsuranceBookRS quoteResponse = restTemplate.postForObject(apiProperties.getTravelexInsurance().getUrl(), params, InsuranceBookRS.class);
        return quoteResponse;
    }

    public InsuranceBookRS purchase(MultiValueMap<String, String> params) {
        InsuranceBookRS purchaseResponse = restTemplate.postForObject(apiProperties.getTravelexInsurance().getPurchaseUrl(), params, InsuranceBookRS.class);
        return purchaseResponse;
    }

    public PaymentConfigurationResponse paymentConfiguration(MultiValueMap<String, String> params) {
        PaymentConfigurationResponse configResponse = restTemplate
                .postForObject(apiProperties.getTravelexInsurance().getTsysUrl(), params, PaymentConfigurationResponse.class);
        return configResponse;
    }

    public String getProductFormNumber(String policyMetaCode) {
        return travelexInsuranceMapper.getProductFormNumber(policyMetaCode);
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        String policyCode = policyMetaCode.getCode();
        log.debug("Product code: {}, product name: {}", policyCode, policyMetaCode.getName());
        MultiValueMap<String, String> requestParams = travelexInsuranceMapper.toMap(request, policyMetaCode, apiProperties.getTravelexInsurance(), policyMeta);
        InsuranceBookRS quoteResponse = quote(requestParams);
        QuoteResult quoteResult = new QuoteResult();
        if (quoteResponse.getWarnings() == null) {
            quoteResult.setStatus(Result.Status.SUCCESS);
            BigDecimal totalPrice = new BigDecimal(quoteResponse.getPlanCost().getAmount());
            Product product = new Product(policyMeta, policyMetaCode, totalPrice, request.getUpsaleValues());
            quoteResult.products.add(product);
        } else {
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.getErrors().add(new Result.Error("-1", quoteResponse.getWarnings().getWarning()));
        }
        return quoteResult;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {
        ObjectMapper mapper = new ObjectMapper();

        String tsysUrl = "https://stagegw.transnox.com/servlets/TransNox_API_Server";
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-agent", "infonox");

        HttpEntity<String> authEntity = new HttpEntity<>("\t{\n" +
                "\t\"GenerateKey\" : {\n" +
                "\t  \"mid\":\"887000003223\",\n" +
                "\t  \"userID\":\"TA5618176\",\n" +
                "\t  \"password\":\"Travel123!\",\n" +
                "\t  \"developerID\":\"003223G001\"\n" +
                "\t}\n" +
                "\t}", headers);

        ResponseEntity<String> exchange = restTemplate.exchange(tsysUrl, HttpMethod.POST, authEntity, String.class);

        String body = exchange.getBody();
        if (StringUtils.isNotBlank(body)) {
            body = body.replace("{\"GenerateKeyResponse\":", "");
            body = body.replace("}}", "}");
        }
        GenerateKeyResponse generateKeyResponse = null;
        try {
            generateKeyResponse = mapper.readValue(body, GenerateKeyResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity<String> tokenEntity;
        if (request.getCreditCard().getCcType() == CardType.VISA || request.getCreditCard().getCcType() == CardType.MasterCard) {
            tokenEntity = new HttpEntity<>("{\n" +
                    "  \"GetOnusToken\": {\n" +
                    "    \"deviceID\": \"88700000322301\",\n" +
                    "    \"transactionKey\": \"" + generateKeyResponse.getTransactionKey() + "\",\n" +
                    "    \"cardDataSource\": \"MANUAL\",\n" +
                    "    \"cardNumber\": \"" + request.getCreditCard().getCcNumber() + "\",\n" +
                    "    \"expirationDate\": \"" + request.getCreditCard().getCcExpMonth() + "/" + request.getCreditCard().getCcExpYear().substring(2, 4) + "\",\n" +
                    "    \"cardHolderName\": \"" + request.getCreditCard().getCcName() + "\",\n" +
                    "    \"cardVerification\": \"YES\",\n" +
                    "    \"developerID\": \"003223G001\"\n" +
                    "  }\n" +
                    "}", headers);
        } else {
            tokenEntity = new HttpEntity<>("{\n" +
                    "  \"GetOnusToken\": {\n" +
                    "    \"deviceID\": \"88700000322301\",\n" +
                    "    \"transactionKey\": \"" + generateKeyResponse.getTransactionKey() +"\",\n" +
                    "    \"cardDataSource\": \"MANUAL\",\n" +
                    "    \"cardNumber\": \"" + request.getCreditCard().getCcNumber() + "\",\n" +
                    "    \"expirationDate\": \"" + request.getCreditCard().getCcExpMonth() + "/" + request.getCreditCard().getCcExpYear().substring(2,4) + "\",\n" +
                    "    \"cardHolderName\": \"" + request.getCreditCard().getCcName() + "\",\n" +
                    "    \"developerID\": \"003223G001\"\n" +
                    "  }\n" +
                    "}", headers);
        }

        ResponseEntity<String> tokenExchange = restTemplate.exchange(tsysUrl, HttpMethod.POST, tokenEntity, String.class);
        String tokenBody = tokenExchange.getBody();
        tokenBody = tokenBody.replace("{\"GetOnusTokenResponse\":", "");
        tokenBody = tokenBody.replace("}}", "}");

        GetOnusTokenResponse getOnusResponse= null;
        try {
            getOnusResponse = mapper.readValue(tokenBody, GetOnusTokenResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> requestParams = travelexInsuranceMapper.toMap(request, apiProperties.getTravelexInsurance(), null, getOnusResponse);
        InsuranceBookRS response = purchase(requestParams);
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        if (response.getWarnings() == null) {
            purchaseResponse.setStatus(Result.Status.SUCCESS);
            purchaseResponse.setPolicyNumber(response.getPolicyDetail().getPolicyNumber());
        } else {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error("-1", response.getWarnings().getWarning()));
        }
        return purchaseResponse;
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.TravelexInsurance;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        if (request.getCitizenCountry() != CountryCode.US && request.getResidentCountry() != CountryCode.US) {
            return Arrays.asList(new Result.Error("-1", "Country of Residence for Non-US citizen must be US!"));
        }
        if (request.getTravelers().size() > 1 &&
                policyMetaCode.getCode().equals(BUSINESS_TRAVELER_SINGLE_CODE)) {
            return Arrays.asList(new Result.Error("-1", "Number of travelers = 1 for Business Traveler plan"));
        }
        return Collections.emptyList();
    }

    public static void addManifestToModel(QuoteRequest quoteRequest, Model model) {

        model.addAttribute("deviceId", DEVICE_ID);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("User-Agent", USER_AGENT);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Get the transaction key
        GenerateKey generateKey = new GenerateKey(MERCHANT_ID, USER_ID, PASSWORD, DEVELOPER_ID);
        GenerateKeyMessage generateKeyMessage = new GenerateKeyMessage(generateKey);
        String generateKeyMessageStr = JsonUtils.getJsonString(generateKeyMessage);
        generateKeyMessageStr = generateKeyMessageStr.replaceAll("generateKey", "GenerateKey");
        HttpEntity<String> requestEntity = new HttpEntity<String>(generateKeyMessageStr, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(ENDPOINT_URL, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        String generateKeyResponseMessageStr = "";
        int p = responseBody.indexOf("GenerateKeyResponse");
        if(p >= 0) {
            generateKeyResponseMessageStr = responseBody.substring(p + GENERATE_KEY_RESPONSE_OFFSET, responseBody.length() - 2);
        }
        GenerateKeyResponse generateKeyResponse = JsonUtils.getObject(generateKeyResponseMessageStr, GenerateKeyResponse.class);
        String transactionKey = generateKeyResponse.getTransactionKey();
        System.out.println("transactionKey=" + transactionKey);

        //Generate the manifest
        String manifest = "";
        if(transactionKey != null) {
            try {
                System.out.println("merchantId=" + MERCHANT_ID);
                System.out.println("deviceId=" + DEVICE_ID);
                System.out.println("tripCost=" + quoteRequest.getTripCost().intValue());
                manifest = TransITCrypt.encryptManifest(MERCHANT_ID, DEVICE_ID, String.valueOf(quoteRequest.getTripCost().intValue()), transactionKey);
                System.out.println("manifest=" + manifest);
            } catch (Exception e) {
                System.out.println("Encryption error = " + e.getMessage());
            }
        } else {
            System.out.println("Error response getting the transaction key from the response body: " + responseBody);
        }
        model.addAttribute("manifest", manifest);
    }
}