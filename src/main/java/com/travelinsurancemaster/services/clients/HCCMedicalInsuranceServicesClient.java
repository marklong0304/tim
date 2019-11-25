package com.travelinsurancemaster.services.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.hccmis.*;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.PolicyQuoteParamService;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.RestUtil;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by ritchie on 3/11/15.
 */
public class HCCMedicalInsuranceServicesClient extends AbstractRestClient {

    private static final Logger log = LoggerFactory.getLogger(HCCMedicalInsuranceServicesClient.class);

    private static final String DATE_FORMAT = "MM/dd/yyyy";

    private static final String STRING = "string";

    public static final String REFER_ID = "26126";
    public static final String CULTURE = "en-US";
    public static final String FULFILLMENT_ONLINE = "1";
    public static final String WILL_WORK_IN_FLORIDA = "0";
    public static final String DEFAULT_GENDER = "M";

    public static final List<StateCode> PHYSICALLY_LOCATED_STATES =  Stream.of(StateCode.MD, StateCode.NY,StateCode.WA).collect(Collectors.toList());
    public static final List<CountryCode> PHYSICALLY_LOCATED_COUNTRIES =  Stream.of(CountryCode.CA, CountryCode.AU).collect(Collectors.toList());

    public static final String HCCMIS_ATLAS_TRAVEL = "HCCMISAtlasTravel";
    public static final String HCCMIS_ATLAS_PREMIUM = "HCCMISAtlasPremium";
    public static final String HCCMIS_ATLAS_ESSENTIAL = "HCCMISAtlasEssential";

    public static final String HCCMIS_MULTITRIP_INTERNATIONAL = "HCCMISMultiTripInternational";
    public static final String HCCMIS_MULTITRIP_US = "HCCMISMultiTripUS";
    public static final String HCCMIS_STUDENT_SECURE_SELECT = "HCCMISStudentSecureSelect";
    private static final String HCCMIS_STUDENT_SECURE_SMART = "HCCMISStudentSecureSmart";
    private static final String HCCMIS_STUDENT_SECURE_BUDGET = "HCCMISStudentSecureBudget";
    private static final String HCCMIS_STUDENT_SECURE_ELITE = "HCCMISStudentSecureElite";

    private static final String INCLUDE_US = "I";
    private static final String EXCLUDE_US = "E";
    private static final String BUDGET = "Budget";
    private static final String SELECT = "Select";
    private static final String SMART = "Smart";
    private static final String ELITE = "Elite";
    private static final String PAYMENT_TYPE = "F";
    public static final String CERT = "Cert";
    private static final String NON_US = "Non-US";
    public static final String KEY = "Key";
    public static final String VALUE = "Value";
    private static final String EXCLUS = "EXCLUS";
    private static final String INCLUS = "INCLUS";

    @Autowired
    private PolicyQuoteParamService policyQuoteParamService;

    @Autowired
    InsuranceMasterApiProperties apiProperties;

    @Autowired
    private HCCMapper hccMapper;

    public HCCMedicalInsuranceServicesClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public String getAtlasTravelPurchaseResponse(MultiValueMap<String, String> params) {
        String response = restTemplate.postForObject(apiProperties.gethCCMISAtlasTravel().getUrl(), params, String.class);
        response = response.replace("\n", "");
        return response;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getStudentSecurePurchaseResponse(HCCMISStudentSecureRequest request) {
        String responseString = restTemplate.postForObject(apiProperties.gethCCMISStudentSecure().getUrl(), request, String.class);
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<Map<String, Object>> response = null;
        try {
            response = objectMapper.readValue(responseString, List.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return response;
    }

    public String getAtlasMultiTripPurchaseResponse(MultiValueMap<String, String> purchaseParams) {
        String response = restTemplate.postForObject(apiProperties.gethCCMISAtlasMultiTrip().getUrl(), purchaseParams, String.class);
        response = response.replace("\r\n", "");
        return response;
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        QuoteResult quoteResult = null;
        switch (policyMetaCode.getCode()) {
            case HCCMIS_ATLAS_TRAVEL:
            case HCCMIS_ATLAS_PREMIUM:
            case HCCMIS_ATLAS_ESSENTIAL:
                quoteResult = quoteAtlasTravel(request, policyMeta, policyMetaCode);
                break;
            case HCCMIS_MULTITRIP_INTERNATIONAL:
            case HCCMIS_MULTITRIP_US:
                quoteResult = quoteMultiTripTravel(request, policyMeta, policyMetaCode);
                break;
            case HCCMIS_STUDENT_SECURE_SELECT:
            case HCCMIS_STUDENT_SECURE_SMART:
            case HCCMIS_STUDENT_SECURE_BUDGET:
            case HCCMIS_STUDENT_SECURE_ELITE:
                quoteResult = quoteStudentSecure(request, policyMeta, policyMetaCode);
                break;
        }
        return quoteResult;
    }

    private QuoteResult quoteMultiTripTravel(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        QuoteResult quoteResult = new QuoteResult();
        quoteResult.setStatus(Result.Status.SUCCESS);
        long days = DAYS.between(request.getDepartDate(), request.getReturnDate()) + 1;
        Long premium = policyQuoteParamService.getPolicyQuoteParam(request, policyMeta, 0l, 0l, Long.valueOf(days), null, null);
        if (premium == null) {
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.getErrors().add(new Result.Error("-1", "wrong days, should be <= 45"));
            return quoteResult;
        }
        BigDecimal price = BigDecimal.valueOf(request.getTravelers().size()).multiply(BigDecimal.valueOf(premium));
        Product product = new Product(policyMeta, policyMetaCode, price, request.getUpsaleValues());
        quoteResult.products.add(product);
        return quoteResult;
    }

    private QuoteResult quoteStudentSecure(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        QuoteResult quoteResult = new QuoteResult();
        Long priceDailyAllTravelers = 0l;
        for (GenericTraveler traveler : request.getTravelers()) {
            Long priceDaily = policyQuoteParamService.getPolicyQuoteParam(request, policyMeta, 0l, 0l, Long.valueOf(traveler.getAge()), null, null);
            if (priceDaily == null) {
                quoteResult.setStatus(Result.Status.ERROR);
                quoteResult.getErrors().add(new Result.Error("-1", "Age does not comply with the deductible limits"));
                return quoteResult;
            }
            priceDailyAllTravelers += priceDaily;
        }
        long days = DAYS.between(request.getDepartDate(), request.getReturnDate()) + 1;
        BigDecimal price;
        Calendar calendar = Calendar.getInstance();
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (days <= daysInMonth) {
            price = BigDecimal.valueOf(priceDailyAllTravelers).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(daysInMonth));
        } else {
            price = BigDecimal.valueOf(priceDailyAllTravelers).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(days));
        }
        quoteResult.setStatus(Result.Status.SUCCESS);
        Product product = new Product(policyMeta, policyMetaCode, price, request.getUpsaleValues());
        quoteResult.products.add(product);
        return quoteResult;
    }

    private QuoteResult quoteAtlasTravel(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {

        QuoteResult quoteResult = new QuoteResult();

        HCCPurchaseRequest hccPurchaseRequest = hccMapper.createQuoteRequest(quoteRequest, policyMeta, policyMetaCode);
        HttpEntity<HCCPurchaseRequest> requestEntity = new HttpEntity<>(hccPurchaseRequest, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange(apiProperties.gethCCMISAtlasTravel().getUrl(), HttpMethod.POST, requestEntity, String.class);
        HCCErrorPurchaseResponse hccQuoteResponse = JsonUtils.getObject(response.getBody(), HCCErrorPurchaseResponse.class);

        if(!RestUtil.isError(response.getStatusCode())) {
            quoteResult.setStatus(Result.Status.SUCCESS);
            if(hccQuoteResponse.getTotal() != null) {
                Product product = new Product(policyMeta, policyMetaCode, new BigDecimal(hccQuoteResponse.getTotal()));
                quoteResult.products.add(product);
            }
        } else {
            quoteResult.setStatus(Result.Status.ERROR);
        }

        return quoteResult;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {
        PurchaseResponse purchaseResponse;
        String policyCode = request.getProduct().getPolicyMetaCode().getCode();
        switch (policyCode) {
            case HCCMIS_ATLAS_TRAVEL:
            case HCCMIS_ATLAS_PREMIUM:
            case HCCMIS_ATLAS_ESSENTIAL:
                purchaseResponse = purchaseAtlasTravel(request);
                break;
            case HCCMIS_STUDENT_SECURE_SELECT:
            case HCCMIS_STUDENT_SECURE_SMART:
            case HCCMIS_STUDENT_SECURE_BUDGET:
            case HCCMIS_STUDENT_SECURE_ELITE:
                purchaseResponse = purchaseStudentSecure(request);
                break;
            case HCCMIS_MULTITRIP_INTERNATIONAL:
            case HCCMIS_MULTITRIP_US:
                purchaseResponse = purchaseAtlasMultiTrip(request);
                break;
            default:
                throw new IllegalArgumentException(policyCode);
        }
        return purchaseResponse;
    }

    @SuppressWarnings("unchecked")
    private PurchaseResponse purchaseStudentSecure(PurchaseRequest request) {
        HCCMISStudentSecureRequest studentSecureRequest = new HCCMISStudentSecureRequest();
        studentSecureRequest.setReferId(REFER_ID);
        studentSecureRequest.setCulture("en-US"); // todo: how to check?
        if (request.getQuoteRequest().getDestinationCountry() == CountryCode.US) {
            studentSecureRequest.setCoverageArea(INCLUDE_US);
        } else {
            studentSecureRequest.setCoverageArea(EXCLUDE_US);
        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        studentSecureRequest.setCoverageBeginDate(dateFormat.format(request.getQuoteRequest().getDepartDate()));
        studentSecureRequest.setCoverageEndDate(dateFormat.format(request.getQuoteRequest().getReturnDate()));
        studentSecureRequest.setDependentCoverage(0); // 0 = no spouse / child. todo?
        studentSecureRequest.setOnlineFulfillment(1);
        studentSecureRequest.setSelectedPaymentType(PAYMENT_TYPE); // todo: what types could be?
        String planType = null;
        switch (request.getProduct().getPolicyMetaCode().getCode()) {
            case HCCMIS_STUDENT_SECURE_BUDGET:
                planType = BUDGET;
                break;
            case HCCMIS_STUDENT_SECURE_SELECT:
                planType = SELECT;
                break;
            case HCCMIS_STUDENT_SECURE_SMART:
                planType = SMART;
                break;
            case HCCMIS_STUDENT_SECURE_ELITE:
                planType = ELITE;
                break;
        }
        studentSecureRequest.setSelectedPlanType(planType);
        GenericTraveler primaryTraveler = null;
        for (GenericTraveler traveler : request.getTravelers()) {
            if (traveler.isPrimary()) {
                primaryTraveler = traveler;
            }
        }
        GenericTraveler traveler = request.getTravelers().get(0);
        studentSecureRequest.setPrimaryBeneficiary(traveler.getBeneficiary());
        studentSecureRequest.setPrimaryCitizenship(WordUtils.capitalizeFully(request.getQuoteRequest().getCitizenCountry().getCaption()));
        studentSecureRequest.setPrimaryBirthDate(dateFormat.format(primaryTraveler.getBirthdaySafe()));
        studentSecureRequest.setPrimaryEligibleRequirements(1);
        studentSecureRequest.setPrimaryEmailAddress(request.getEmail());
        studentSecureRequest.setPrimaryGender("M"); // todo?
        studentSecureRequest.setPrimaryHomeCountry(WordUtils.capitalizeFully(request.getQuoteRequest().getResidentCountry().getCaption()));
        studentSecureRequest.setPrimaryHostCountry(WordUtils.capitalizeFully(request.getQuoteRequest().getDestinationCountry().getCaption()));
        studentSecureRequest.setPrimaryMailAddress(request.getAddress());
        studentSecureRequest.setPrimaryMailCity(request.getCity());
        studentSecureRequest.setPrimaryMailCountry(WordUtils.capitalizeFully(request.getQuoteRequest().getResidentCountry().getCaption()));
        if (request.getQuoteRequest().getResidentCountry() == CountryCode.US) {
            studentSecureRequest.setPrimaryMailState(request.getQuoteRequest().getResidentState().name());
        } else {
            studentSecureRequest.setPrimaryMailState(NON_US);
        }
        if (request.getQuoteRequest().getResidentState() == StateCode.WA || request.getQuoteRequest().getResidentState() == StateCode.NY
                || request.getQuoteRequest().getResidentState() == StateCode.MD || request.getQuoteRequest().getResidentCountry() == CountryCode.CA) {
            studentSecureRequest.setPhysicallyLocated(1);
        } else {
            studentSecureRequest.setPhysicallyLocated(0);
        }
        studentSecureRequest.setPrimaryMailZip(request.getPostalCode());
        studentSecureRequest.setPrimaryNameFirst(primaryTraveler.getFirstName());
        studentSecureRequest.setPrimaryNameLast(primaryTraveler.getLastName());
        studentSecureRequest.setPrimaryNameMiddle(primaryTraveler.getMiddleInitials());
        studentSecureRequest.setPrimaryPhone(request.getPhone());
        studentSecureRequest.setPrimaryStudentScholarStatus(2); // todo
        studentSecureRequest.setPrimaryUniversityName("universityName"); // todo
        if (request.getQuoteRequest().getResidentCountry() != CountryCode.US) {
            studentSecureRequest.setPrimaryUsCitizenOrResident(0);
        } else {
            studentSecureRequest.setPrimaryUsCitizenOrResident(1);
        }
        studentSecureRequest.setPrimaryUsState("");
        studentSecureRequest.setPrimaryVisaType("F-1"); // todo
        HCCMISStudentSecureRequest.CreditCard creditCard = studentSecureRequest.new CreditCard();
        creditCard.setCardExpirationMonth(request.getCreditCard().getCcExpMonth());
        creditCard.setCardExpirationYear(request.getCreditCard().getCcExpYear());
        creditCard.setCardHolderAddress(request.getCreditCard().getCcAddress());
        creditCard.setCardHolderCity(request.getCreditCard().getCcCity());
        creditCard.setCardHolderCountry(request.getCreditCard().getCcCountry().getCaption());
        creditCard.setCardHolderName(request.getCreditCard().getCcName());
        creditCard.setCardHolderState(request.getCreditCard().getCcStateCode() != null ? request.getCreditCard().getCcStateCode().name() : "NA");
        creditCard.setCardHolderZip(request.getCreditCard().getCcZipCode());
        creditCard.setCardNumber(request.getCreditCard().getCcNumber());
        creditCard.setCardSecurityCode(request.getCreditCard().getCcCode());
        studentSecureRequest.setCreditCard(creditCard);
        List<Map<String, Object>> purchaseResponse = getStudentSecurePurchaseResponse(studentSecureRequest);
        PurchaseResponse response = new PurchaseResponse();
        Object object = purchaseResponse.get(0).get(VALUE);
        if (purchaseResponse.get(0).get(KEY).equals(CERT)) {
            response.setStatus(Result.Status.SUCCESS);
            response.setPolicyNumber((String) purchaseResponse.get(0).get(VALUE));
        } else {
            response.setStatus(Result.Status.ERROR);
            if (object instanceof String) {
                response.getErrors().add(new Result.Error("-1", (String) object));
            } else {
                for (String error : ((ArrayList<String>) object)) {
                    response.getErrors().add(new Result.Error("-1", error));
                }
            }
        }
        return response;
    }

    private PurchaseResponse purchaseAtlasMultiTrip(PurchaseRequest request) {
        HCCMISAtlasMultiTripRequest atlasMultiTripRequest = new HCCMISAtlasMultiTripRequest();
        GenericTraveler primaryTraveler = null;
        for (GenericTraveler traveler : request.getTravelers()) {
            if (traveler.isPrimary()) {
                primaryTraveler = traveler;
                break;
            }
        }
        atlasMultiTripRequest.setPrimaryFirstName(primaryTraveler.getFirstName());
        atlasMultiTripRequest.setPrimaryMiddleInitial(primaryTraveler.getMiddleInitials());
        atlasMultiTripRequest.setPrimaryLastName(primaryTraveler.getLastName());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        atlasMultiTripRequest.setDateOfBirth(dateFormat.format(primaryTraveler.getBirthdaySafe()));
        atlasMultiTripRequest.setAddress1(request.getAddress());
        atlasMultiTripRequest.setCity(request.getCity());
        atlasMultiTripRequest.setState(request.getQuoteRequest().getResidentState() != null ? request.getQuoteRequest().getResidentState().name() : null);
        atlasMultiTripRequest.setCountry(request.getQuoteRequest().getResidentCountry().name());
        atlasMultiTripRequest.setZip(request.getPostalCode());
        atlasMultiTripRequest.setTelephone(request.getPhone());
        atlasMultiTripRequest.setEmail(request.getEmail());
        atlasMultiTripRequest.setCountryOfCitizenship(request.getQuoteRequest().getCitizenCountry().getCaption());
        atlasMultiTripRequest.setRequestedEffectiveDate(dateFormat.format(request.getQuoteRequest().getDepartDate())); // todo: what value? now equals to depart date);
        atlasMultiTripRequest.setDepartureDate(dateFormat.format(request.getQuoteRequest().getDepartDate()));
        atlasMultiTripRequest.setReturnDate(dateFormat.format(request.getQuoteRequest().getReturnDate()));
        atlasMultiTripRequest.setCountriesToBeVisited(request.getQuoteRequest().getDestinationCountry().getCaption()); // todo: several countries can be added?
        GenericTraveler traveler = request.getTravelers().get(0);
        atlasMultiTripRequest.setBeneficiaryName(traveler.getBeneficiary());
        String policyType;
        String policyCode = request.getProduct().getPolicyMetaCode().getCode();
        switch (policyCode) {
            case HCCMIS_MULTITRIP_INTERNATIONAL:
                policyType = EXCLUS;
                break;
            case HCCMIS_MULTITRIP_US:
                policyType = INCLUS;
                break;
            default:
                throw new IllegalArgumentException(policyCode);
        }
        atlasMultiTripRequest.setPolicySelected(policyType);
        atlasMultiTripRequest.setPolicyCost(request.getProduct().getTotalPrice());
        atlasMultiTripRequest.setCardNumber(request.getCreditCard().getCcNumber().toString());
        atlasMultiTripRequest.setExpMonthYear(request.getCreditCard().getCcExpMonth() + "/" + request.getCreditCard().getCcExpYear()); // "MM/yy"
        atlasMultiTripRequest.setCardName(request.getCreditCard().getCcName());
        atlasMultiTripRequest.setBillingAddress1(request.getCreditCard().getCcAddress());
        atlasMultiTripRequest.setBillingCity(request.getCreditCard().getCcCity());
        atlasMultiTripRequest.setBillingState(request.getCreditCard().getCcStateCode() != null ? request.getCreditCard().getCcStateCode().name() : null);
        atlasMultiTripRequest.setBillingZip(request.getCreditCard().getCcZipCode());
        atlasMultiTripRequest.setBillingCountry(request.getCreditCard().getCcCountry().getCaption());
        int quotedTerm = (int) DAYS.between(request.getQuoteRequest().getDepartDate(), request.getQuoteRequest().getReturnDate()) + 1;
        atlasMultiTripRequest.setQuotedTerm(quotedTerm); // todo: is quoted term a difference between return and depart days?
        atlasMultiTripRequest.setFulfillment(FULFILLMENT_ONLINE);
        if (request.getQuoteRequest().getCitizenCountry() == CountryCode.US) {
            atlasMultiTripRequest.setHomeCountry(CountryCode.US.name());
        } else {
            atlasMultiTripRequest.setHomeCountry(request.getQuoteRequest().getResidentCountry().name());
        }
        if (request.getQuoteRequest().getResidentState() == StateCode.NY
                || request.getQuoteRequest().getResidentState() == StateCode.MD
                || request.getQuoteRequest().getResidentState() == StateCode.WA) {
            atlasMultiTripRequest.setPhysicallyInStateNyWaMd("1");
        } else {
            atlasMultiTripRequest.setPhysicallyInStateNyWaMd("0");
        }
        if (request.getQuoteRequest().getResidentCountry() == CountryCode.CA) {
            atlasMultiTripRequest.setPhysicallyInCanada("1");
        } else {
            atlasMultiTripRequest.setPhysicallyInCanada("0");
        }
        if (quotedTerm <= 30) {
            atlasMultiTripRequest.setMaxTripDuration("30");
        } else {
            atlasMultiTripRequest.setMaxTripDuration("45");
        }
        String requestString = atlasMultiTripRequest.buildStringRequest();
        MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
        purchaseParams.add(STRING, requestString);
        String purchaseResponse = getAtlasMultiTripPurchaseResponse(purchaseParams);
        PurchaseResponse response = new PurchaseResponse();
        if (purchaseResponse.matches("[A-Z]\\d{7,}")) {
            response.setStatus(Result.Status.SUCCESS);
            response.setPolicyNumber(purchaseResponse);
        } else {
            response.setStatus(Result.Status.ERROR);
            response.getErrors().add(new Result.Error("-1", purchaseResponse));
        }
        return response;
    }

    private PurchaseResponse purchaseAtlasTravel(PurchaseRequest purchaseRequest) {

        HCCPurchaseRequest hccPurchaseRequest = hccMapper.createPurchaseRequest(purchaseRequest);

        HttpEntity<HCCPurchaseRequest> requestEntity = new HttpEntity<>(hccPurchaseRequest, createAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(apiProperties.gethCCMISAtlasTravel().getUrl(), HttpMethod.POST, requestEntity, String.class);

        PurchaseResponse purchaseResponse = new PurchaseResponse();
        if(!RestUtil.isError(response.getStatusCode())) {
            HCCOKPurchaseResponse hccOKPurchaseResponse = null;
            HCCErrorPurchaseResponse hccErrorPurchaseResponse = null;
            try {
                hccOKPurchaseResponse = JsonUtils.getObject(response.getBody(), HCCOKPurchaseResponse.class);
                purchaseResponse.setStatus(Result.Status.SUCCESS);
                purchaseResponse.setPolicyNumber(hccOKPurchaseResponse.getAuthCode());
            } catch(Exception e) {
                hccErrorPurchaseResponse = JsonUtils.getObject(response.getBody(), HCCErrorPurchaseResponse.class);
                purchaseResponse.setStatus(Result.Status.ERROR);
                purchaseResponse.getErrors().add(new Result.Error("Error message", hccErrorPurchaseResponse.getErrorMessage()));
            }
        } else {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error(response.getStatusCode().toString(), response.getStatusCode().getReasonPhrase()));
        }

        return purchaseResponse;
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.HCCMedicalInsuranceServices;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        String policyCode = policyMetaCode.getCode();
        switch (policyMetaCode.getCode()) {
            case HCCMIS_STUDENT_SECURE_SELECT:
            case HCCMIS_STUDENT_SECURE_SMART:
            case HCCMIS_STUDENT_SECURE_BUDGET:
            case HCCMIS_STUDENT_SECURE_ELITE:
                if ((request.getResidentCountry() == CountryCode.US || request.getCitizenCountry() == CountryCode.US)
                        && request.getDestinationCountry() == CountryCode.US) {
                    return Arrays.asList(new Result.Error("-1", policyCode + "plan is not available for US citizens/residents traveling in the US"));
                }
                break;
            case HCCMIS_MULTITRIP_INTERNATIONAL:
                if (request.getCitizenCountry() != CountryCode.US && request.getDestinationCountry() == CountryCode.US) {
                    return Arrays.asList(new Result.Error("-1", policyCode + "plan is not available for non-US citizens traveling in the US"));
                }
                break;
            case HCCMIS_MULTITRIP_US:
                if (request.getCitizenCountry() == CountryCode.US) {
                    return Arrays.asList(new Result.Error("-1", policyCode + "plan is not available for US citizens"));
                }
                if (request.getCitizenCountry() != CountryCode.US && request.getDestinationCountry() != CountryCode.US) {
                    return Arrays.asList(new Result.Error("-1", policyCode + "plan is not available for non-US citizens traveling in the non-US territories"));
                }
                break;
        }
        return Collections.emptyList();
    }

    private HttpHeaders createAuthHeaders(){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return requestHeaders;
    }

}