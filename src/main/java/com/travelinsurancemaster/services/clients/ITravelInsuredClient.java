package com.travelinsurancemaster.services.clients;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelinsurancemaster.clients.itravelinsured.PurchaseResponse;
import com.travelinsurancemaster.clients.itravelinsured.*;
import com.travelinsurancemaster.model.*;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.AbstractSoapClient;
import com.travelinsurancemaster.services.itravelinsured.CountryArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Alexander.Isaenco
 */
public class ITravelInsuredClient extends AbstractSoapClient {
    private static final Logger log = LoggerFactory.getLogger(ITravelInsuredClient.class);

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(ITravelInsuredClient.DATE_FORMAT);

    private static String CERT_PATH_SE = "https://www.itravelinsured.com/travel-insurance/travel-se-insurance/policy-wording";
    private static String CERT_PATH_LX = "https://www.itravelinsured.com/travel-insurance/travel-lx-insurance/policy-wording";
    private static String CERT_PATH_LITE = "https://www.itravelinsured.com/travel-insurance/travel-lite-insurance/policy-wording";

    public static final String TRAVEL_LITE_CODE = "99";
    public static final String TRAVEL_LITE_UNIQUE_CODE = "ITI99";
    private static final String CREDIT_CARD = "0";
    private static final String SEND_CVL_VIA_POSTAL = "true";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String AIR_ONLY = "1";
    private static final String CRUISE_ONLY = "3";
    private static final String AIR_AND_LAND = "2";
    private static final String CRUISE_AND_AIR = "4";
    private static final String LAND_ONLY = "5";
    private static final String AIR_AND_LAND_AND_CRUISE = "6";
    private static final String LAND_AND_CRUISE = "8";

    public HelloResponse hello(Hello helloRequest) {
        log.info("hello() {}", helloRequest);
        HelloResponse response = (HelloResponse) getWebServiceTemplate().marshalSendAndReceive(helloRequest,
                new SoapActionCallback(
                        "http://itravelinsured.com/Hello"));
        return response;
    }

    public QuoteResponse quote(Quote quoteRequest) {
        log.info("quote() {}", quoteRequest);
        QuoteResponse response = (QuoteResponse) getWebServiceTemplate().marshalSendAndReceive(quoteRequest,
                new SoapActionCallback(
                        "http://itravelinsured.com/Quote"));
        return response;
    }

    public PurchaseResponse purchase(Purchase purchaseRequest) {
        log.info("purchase() {}", purchaseRequest);
        PurchaseResponse response = (PurchaseResponse) getWebServiceTemplate().marshalSendAndReceive(purchaseRequest,
                new SoapActionCallback(
                        "http://itravelinsured.com/Purchase"));
        return response;
    }

    public ValidateAgentResponse validateAgent(ValidateAgent validateAgentRequest) {
        log.info("validateAgent() {}", validateAgentRequest);
        ValidateAgentResponse response = (ValidateAgentResponse) getWebServiceTemplate().marshalSendAndReceive(validateAgentRequest,
                new SoapActionCallback(
                        "http://itravelinsured.com/ValidateAgent"));
        return response;
    }

    public SendProposalEmailResponse sendProposalEmail(SendProposalEmail sendProposalEmailRequest) {
        log.info("sendProposalEmail() {}", sendProposalEmailRequest);
        SendProposalEmailResponse response = (SendProposalEmailResponse) getWebServiceTemplate().marshalSendAndReceive(sendProposalEmailRequest,
                new SoapActionCallback(
                        "http://itravelinsured.com/SendProposalEmail"));
        return response;
    }

    public GeneratePolicyNumberResponse generatePolicyNumber(GeneratePolicyNumber generatepolicyNumberRequest) {
        log.info("generatePolicyNumber() {}", generatepolicyNumberRequest);
        GeneratePolicyNumberResponse response = (GeneratePolicyNumberResponse) getWebServiceTemplate().marshalSendAndReceive(generatepolicyNumberRequest,
                new SoapActionCallback(
                        "http://itravelinsured.com/GeneratePolicyNumber"));
        return response;
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        log.info("quoteInternal() {} {} {}", request, policyMeta, policyMetaCode);
        String policyCode = policyMetaCode.getCode();
        log.debug("Product code: {}, product name: {}", policyCode, policyMetaCode.getName());

        if(request.getUpsaleValues().containsKey(CategoryCodes.RENTAL_CAR))
            policyCode = "111";

//        if(request.getPlanType().getId() == PlanType.LIMITED.getId())
//            request.setTripCost(BigDecimal.valueOf(0));

       // request.getUpsaleValues().clear();

        Quote quoteRequest = new Quote();
        quoteRequest.setUserId(apiProperties.getiTravelInsured().getUser());
        quoteRequest.setPassword(apiProperties.getiTravelInsured().getPassword());
        quoteRequest.setState(request.getResidentState() != null ? request.getResidentState().name() : null);
        quoteRequest.setDepartDate(dateFormat.format(request.getDepartDate()));
        quoteRequest.setReturnDate(dateFormat.format(request.getReturnDate()));
        quoteRequest.setPolicyType(policyCode);


        ArrayList<Traveler> travelersList = new ArrayList<>();

        for (GenericTraveler genericTraveler : request.getTravelers()) {
            int birthYear = Calendar.getInstance().get(Calendar.YEAR) - genericTraveler.getAge();
            Traveler traveler = populateTraveler(null,
                                                 null,
                                                 request.getTravelers().get(0).getTripCost(),
                                                 birthYear,
                                                 apiProperties.getiTravelInsured().getReferralFeePercentage(),
                                                 apiProperties.getiTravelInsured().getReferralFeeDollarAmt(),
                                                 false);

            if(request.getPlanType().getId() == PlanType.LIMITED.getId())
                traveler.setTripCost(BigDecimal.valueOf(0));

            travelersList.add(traveler);
        }

        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        // Traveler for Quote contains only 4 filled values, others are null
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(travelersList);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        quoteRequest.setTravelers(jsonString);
        ITravelResponse result = quote(quoteRequest).getQuoteResult();

        QuoteResult quoteResult = new QuoteResult();
        if (result.isSuccess()) {
            quoteResult.setStatus(Result.Status.SUCCESS);
            Quote2 quoteResponse = result.getQuote();

            String certificate = "";
            if(policyMetaCode.getName().compareToIgnoreCase("Travel SE") == 0)
                certificate = CERT_PATH_SE;
            else if(policyMetaCode.getName().compareToIgnoreCase("Travel LX") == 0)
                certificate = CERT_PATH_LX;
            else
                certificate = CERT_PATH_LITE;

            Product product = new Product(policyMeta, policyMetaCode, quoteResponse.getTotalPremium(), request.getUpsaleValues());
//            Product product = new Product(policyMeta, policyMetaCode, quoteResponse.getTotalPremium(), request.getUpsaleValues(), certificate);
            quoteResult.products.add(product);
        } else {
            quoteResult.setStatus(Result.Status.ERROR);
            for (Notice notice : result.getErrors().getNotice()) {
                quoteResult.getErrors().add(new Result.Error(notice.getCode(), notice.getMessage() + " - " + notice.getDetails()));
            }
        }

        return quoteResult;
    }

    public static Traveler populateTraveler(String firstName,
                                            String lastName,
                                            BigDecimal tripCost,
                                            int birthYear,
                                            BigDecimal referralFeePercentage,
                                            BigDecimal referralFeeDollarAmt,
                                            boolean isPrimary) {
        log.info("populateTraveler() {} {} {}", firstName, lastName, tripCost);
        Traveler traveler = new Traveler();
        traveler.setFirstName(firstName);
        traveler.setLastName(lastName);
        traveler.setTripCost(tripCost);
        traveler.setBirthYear(birthYear);
        traveler.setReferralFeePercentage(referralFeePercentage); // Defaults to 0 if blank
        traveler.setReferralFeeDollarAmt(referralFeeDollarAmt); // Defaults to 0 if blank
        traveler.setIsPrimary(isPrimary);
        return traveler;
    }

    @Override
    protected com.travelinsurancemaster.model.webservice.common.PurchaseResponse purchaseInternal(PurchaseRequest request) {
        log.info("purchaseInternal() {} ", request);
        Purchase purchaseRequest = new Purchase();
        purchaseRequest.setUserId(apiProperties.getiTravelInsured().getUser());
        purchaseRequest.setPassword(apiProperties.getiTravelInsured().getPassword());
        purchaseRequest.setState(request.getQuoteRequest().getResidentState() != null ? request.getQuoteRequest().getResidentState().name() : null);
        purchaseRequest.setDepartDate(dateFormat.format(request.getQuoteRequest().getDepartDate()));
        purchaseRequest.setReturnDate(dateFormat.format(request.getQuoteRequest().getReturnDate()));
        purchaseRequest.setPolicyType(request.getProduct().getPolicyMetaCode().getCode());

        ArrayList<Traveler> travelersPurchase = new ArrayList<>();
        for (GenericTraveler genericTraveler : request.getTravelers()) {
            int birthYear = Calendar.getInstance().get(Calendar.YEAR) - genericTraveler.getAge();
            Traveler traveler = populateTraveler(genericTraveler.getFirstName(),
                                                 genericTraveler.getLastName(),
                                                 genericTraveler.getTripCost(),
                                                 birthYear,
                                                 apiProperties.getiTravelInsured().getReferralFeePercentage(),
                                                 apiProperties.getiTravelInsured().getReferralFeeDollarAmt(),
                                                 genericTraveler.isPrimary());
            travelersPurchase.add(traveler);
        }
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        // Traveler for Quote contains only 4 filled values
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(travelersPurchase);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        purchaseRequest.setTravelers(jsonString);
        purchaseRequest.setProcessor(""); // optional
        purchaseRequest.setEmail(apiProperties.getiTravelInsured().getAgentEmail()); // agent's email
        purchaseRequest.setGroupName(""); // optional
        purchaseRequest.setAddress1(request.getAddress());
        purchaseRequest.setAddress2(""); // optional
        purchaseRequest.setCity(request.getCity());
        purchaseRequest.setPostalCode(request.getPostalCode());
        purchaseRequest.setCountry(request.getQuoteRequest().getResidentCountry() != null ? request.getQuoteRequest().getResidentCountry().name() : null);
        purchaseRequest.setPhone(request.getPhone());
        purchaseRequest.setTravelerEmail(request.getEmail()); // optional
        purchaseRequest.setSendCvlViaPostal(SEND_CVL_VIA_POSTAL);
        String tripType = getTripType(request.getTripTypes());
        purchaseRequest.setTripType(tripType);
        purchaseRequest.setTripDestination(CountryArea.getAreaByCode(request.getQuoteRequest().getDestinationCountry().name()));
        purchaseRequest.setOutsideAgentPin(""); // optional
        purchaseRequest.setAgentInvoice(""); // optional
        purchaseRequest.setPaymentMethod(CREDIT_CARD);
        purchaseRequest.setPayeeFirstName(request.getCreditCard().getCcName());
        purchaseRequest.setPayeeLastName(" "); // blank, one field for FirstName and LastName
        purchaseRequest.setPayeeAddress(request.getCreditCard().getCcAddress());
        purchaseRequest.setPayeeCity(request.getCreditCard().getCcCity());
        StateCode ccStateCode = request.getCreditCard().getCcStateCode();
        purchaseRequest.setPayeeState(ccStateCode != null ? ccStateCode.name() : null);
        purchaseRequest.setPayeePostalCode(request.getCreditCard().getCcZipCode());
        CountryCode ccCountry = request.getCreditCard().getCcCountry();
        purchaseRequest.setPayeeCountry(ccCountry != null ? ccCountry.name() : null);
        purchaseRequest.setCardNumber(String.valueOf(request.getCreditCard().getCcNumber()));
        String type;
        switch (request.getCreditCard().getCcType()) {
            case VISA:
                type = "VI";
                break;
            case MasterCard:
                type = "MC";
                break;
            case AmericanExpress:
                type = "AX";
                break;
            case Discover:
                type = "DI";
                break;
            default:
                type = "";
        }
        purchaseRequest.setCardBrand(type);
        purchaseRequest.setExpMonth(request.getCreditCard().getCcExpMonth());
        purchaseRequest.setExpYear(request.getCreditCard().getCcExpYear());
        purchaseRequest.setBankName(""); // optional
        purchaseRequest.setBankAcctNum(""); // optional
        purchaseRequest.setBankABACode(""); // optional
        PurchaseResponse response = purchase(purchaseRequest);
        com.travelinsurancemaster.model.webservice.common.PurchaseResponse purchaseResponse = new com.travelinsurancemaster.model.webservice.common.PurchaseResponse();
        if (response.getPurchaseResult().isSuccess()) {
            purchaseResponse.setStatus(Result.Status.SUCCESS);
            purchaseResponse.setPolicyNumber(response.getPurchaseResult().getPurchaseResponse().getPolicyNo());
        } else {
            purchaseResponse.setStatus(Result.Status.ERROR);
            List<Notice> notices = response.getPurchaseResult().getErrors().getNotice();
            for (Notice notice : notices) {
                purchaseResponse.getErrors().add(new Result.Error(notice.getCode(), notice.getMessage() + " - " + notice.getDetails()));
            }
        }
        return purchaseResponse;
    }

    private String getTripType(Set<String> tripTypes) {
        log.info("getTripType() {} ", tripTypes);
        if (tripTypes.size() == 1) {
            if (tripTypes.contains(TripType.Air.name())) {
                return AIR_ONLY;
            }
            if (tripTypes.contains(TripType.Cruise.name())) {
                return CRUISE_ONLY;
            }
            if (tripTypes.contains(TripType.Tour.name())) {
                return LAND_ONLY;
            }
        }
        if (tripTypes.size() == 2) {
            if (tripTypes.contains(TripType.Air.name()) && tripTypes.contains(TripType.Tour.name())) {
                return AIR_AND_LAND;
            }
            if (tripTypes.contains(TripType.Cruise.name()) && tripTypes.contains(TripType.Air.name())) {
                return CRUISE_AND_AIR;
            }
            if (tripTypes.contains(TripType.Tour.name()) && tripTypes.contains(TripType.Cruise.name())) {
                return LAND_AND_CRUISE;
            }
        }
        return AIR_AND_LAND_AND_CRUISE;
    }

    @Override
    public String getVendorCode() {
//        log.info("getVendorCode()");
        return ApiVendor.ITravelInsured;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        log.info("validateQuoteRequest() {} {} ", request, policyMetaCode);
        return Collections.emptyList();
    }
}
