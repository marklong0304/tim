package com.travelinsurancemaster.services.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.tripmate.*;
import com.travelinsurancemaster.model.webservice.tripmate.travelsafe.TravelSafeBaseQuery;
import com.travelinsurancemaster.model.webservice.tripmate.travelsafe.TravelSafeBookRequest;
import com.travelinsurancemaster.model.webservice.tripmate.travelsafe.TravelSafeQuoteRequest;
import com.travelinsurancemaster.services.AbstractRestClient;
import com.travelinsurancemaster.services.CategoryCacheService;
import com.travelinsurancemaster.services.PolicyMetaCategoryValueService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.tripmate.DestinationCodes;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// todo: make TravelSafeClient extends TripMateClient
public class TravelSafeClient extends AbstractRestClient {
    private static final Logger log = LoggerFactory.getLogger(TravelSafeClient.class);

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(TripMateRequestConstants.DATE_FORMAT);

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private CategoryCacheService categoryCacheService;

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    private Map<String, Integer> flightOnlyAdAndDMap;

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

    public static final String OPTION249 = "option249";
    public static final String OPTION250 = "option250";
    public static final String OPTION251 = "option251";
    public static final String OPTION252 = "option252";
    public static final String OPTION253 = "option253";
    public static final String OPTION254 = "option254";
    public static final String OPTION255 = "option255";
    public static final String OPTION256 = "option256";
    public static final String OPTION257 = "option257";
    private static final String TOUROP_VALUE = "505";
    private static final String CRUISELINE_VALUE = "505";
    private static final String MISCPROV_VALUE = "505";
    public static final String BASIC_PLAN = "Basic";
    public static final String CLASSIC_PLAN = "Classic";
    public static final String CLASSIC_PLAN_PLUS = "Classic Plus";
    public static final String BASIC_PLAN_CODE = "TS6028";
    public static final String CLASSIC_PLAN_CODE = "TS6029";
    public static final String CLASSIC_PLAN_PLUS_CODE = "TS6030";

    public TravelSafeClient(RestTemplate restTemplate) {
        super(restTemplate);
        initFlightOnlyAdAndDMap();
    }

    private void initFlightOnlyAdAndDMap() {
        flightOnlyAdAndDMap = new HashMap<>();
        flightOnlyAdAndDMap.put("100000", 10);
        flightOnlyAdAndDMap.put("250000", 25);
        flightOnlyAdAndDMap.put("500000", 50);
    }

    public TripMateBookResponse book(TravelSafeBookRequest bookRequest) throws IOException {
        MultiValueMap<String, String> params = parseBookRequest(bookRequest);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(apiProperties.getTravelSafe().getPurchaseUrl(), HttpMethod.POST, requestEntity, String.class);
        String responseStr = response.getBody().substring(response.getBody().indexOf("{"));
        TripMateBookResponse resp = mapper.readValue(responseStr, TripMateBookResponse.class);
        return resp;
    }

    public TripMateQuoteResponse quote(TravelSafeQuoteRequest quoteRequest) throws IOException {
        MultiValueMap<String, String> params = parseQuoteRequest(quoteRequest);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(apiProperties.getTravelSafe().getQuoteUrl(), HttpMethod.POST, requestEntity, String.class);

        TripMateQuoteResponse resp = mapper.readValue(response.getBody(), TripMateQuoteResponse.class);

        return resp;
    }

    private MultiValueMap<String, String> parseQuoteRequest(TravelSafeQuoteRequest quoteRequest) {
        MultiValueMap<String, String> quoteQueryMap = new LinkedMultiValueMap<>();
        parseBase(quoteRequest, quoteQueryMap, false);
        return quoteQueryMap;
    }

    private void parseBase(TravelSafeBaseQuery query, MultiValueMap<String, String> bookQueryMap, boolean book) {

        for (int i = 1; i <= query.travelers.size(); i++) {
            TripMateTraveler traveler = query.travelers.get(i - 1);

            String travelerIndex = TripMateRequestConstants.TRAVELER_PREFIX + i + "_";
            bookQueryMap.add(travelerIndex + TripMateRequestConstants.AGE, traveler.age.toString());
            bookQueryMap.add(travelerIndex + TripMateRequestConstants.TRIPCOST, String.valueOf(traveler.tripcost.intValue()));
            bookQueryMap.add(travelerIndex + TripMateRequestConstants.DEPARTDATE, dateFormat.format(traveler.departdate));
            bookQueryMap.add(travelerIndex + TripMateRequestConstants.RETURNDATE, dateFormat.format(traveler.returndate));

            if (book) {
                bookQueryMap.add(travelerIndex + TripMateRequestConstants.PREFIX, traveler.prefix);
                bookQueryMap.add(travelerIndex + TripMateRequestConstants.FIRSTNAME, traveler.firstname);
                bookQueryMap.add(travelerIndex + TripMateRequestConstants.LASTNAME, traveler.lastname);
                bookQueryMap.add(travelerIndex + TripMateRequestConstants.MIDDLENAME, traveler.middlename);
                bookQueryMap.add(travelerIndex + TripMateRequestConstants.SUFFIX, traveler.suffix);
                bookQueryMap.add(travelerIndex + TripMateRequestConstants.DOB, traveler.dob);
                bookQueryMap.add(travelerIndex + TripMateRequestConstants.GENDER, traveler.gender);
                bookQueryMap.add(travelerIndex + TripMateRequestConstants.TRIPLENGTH, traveler.triplength.toString());
                bookQueryMap.add(travelerIndex + TripMateRequestConstants.DEPOSITDATE, traveler.depositdate != null ? dateFormat.format(traveler.depositdate) : "");
            }
        }

        bookQueryMap.add(TripMateRequestConstants.LOCATION, query.location);
        bookQueryMap.add(TripMateRequestConstants.UNAME, query.uname);
        bookQueryMap.add(TripMateRequestConstants.PWD, query.pwd);
        bookQueryMap.add(TripMateRequestConstants.TOTAL_TRAVS, query.total_travs);
        bookQueryMap.add(TripMateRequestConstants.INDIVIDUAL_DATES, query.individual_dates);
        bookQueryMap.add(TripMateRequestConstants.COUNTRY, query.country);
        bookQueryMap.add(TripMateRequestConstants.STATE, query.state);
        bookQueryMap.add(TripMateRequestConstants.DEPARTDATE, dateFormat.format(query.departdate));
        bookQueryMap.add(TripMateRequestConstants.RETURNDATE, dateFormat.format(query.returndate));
    }

    private MultiValueMap<String, String> parseBookRequest(TravelSafeBookRequest bookRequest) {

        MultiValueMap<String, String> bookQueryMap = new LinkedMultiValueMap<>();

        parseBase(bookRequest, bookQueryMap, true);

        bookQueryMap.add(TripMateRequestConstants.PRODUCTID, bookRequest.productID);
        bookQueryMap.add(TripMateRequestConstants.TRAVELER_COUNT, bookRequest.traveler_count);
        bookQueryMap.add(TripMateRequestConstants.BOOKING_NUMBER, bookRequest.booking_number);
        bookQueryMap.add(TripMateRequestConstants.BENEFICIARY, bookRequest.beneficiary);
        bookQueryMap.add(TripMateRequestConstants.TRIPCOST, bookRequest.tripcost);
        bookQueryMap.add(TripMateRequestConstants.EMAIL, bookRequest.email);
        bookQueryMap.add(TripMateRequestConstants.FAX, bookRequest.fax);
        bookQueryMap.add(TripMateRequestConstants.PHONE1, bookRequest.phone1);
        bookQueryMap.add(TripMateRequestConstants.ADDRESS1, bookRequest.address1);
        bookQueryMap.add(TripMateRequestConstants.ADDRESS2, bookRequest.address2);
        bookQueryMap.add(TripMateRequestConstants.ZIP, bookRequest.zip);
        bookQueryMap.add(TripMateRequestConstants.ZIP4, bookRequest.zip4);
        bookQueryMap.add(TripMateRequestConstants.CITY, bookRequest.city);
        bookQueryMap.add(TripMateRequestConstants.TRIP_LENGTH, bookRequest.trip_length);
        bookQueryMap.add(TripMateRequestConstants.DEPOSITDATE, bookRequest.depositdate != null ? dateFormat.format(bookRequest.depositdate) : "");
        bookQueryMap.add(TripMateRequestConstants.RENTALCAR, bookRequest.rentalcar);
        bookQueryMap.add(TripMateRequestConstants.PREMIUM, bookRequest.premium);
        bookQueryMap.add(TripMateRequestConstants.DESTINATION, bookRequest.destination);
        bookQueryMap.add(TripMateRequestConstants.TRIPTYPE, bookRequest.triptype);
        bookQueryMap.add(TripMateRequestConstants.AIRLINE, bookRequest.airline);
        bookQueryMap.add(TripMateRequestConstants.TOUROP, bookRequest.tourop);
        bookQueryMap.add(TripMateRequestConstants.CRUISELINE, bookRequest.cruiseline);
        bookQueryMap.add(TripMateRequestConstants.MISCPROV, bookRequest.miscprov);
        bookQueryMap.add(OPTION249, bookRequest.option249);
        bookQueryMap.add(OPTION250, bookRequest.option250);
        bookQueryMap.add(OPTION251, bookRequest.option251);
        bookQueryMap.add(OPTION252, bookRequest.option252);
        bookQueryMap.add(OPTION253, bookRequest.option253);
        bookQueryMap.add(OPTION254, bookRequest.option254);
        bookQueryMap.add(OPTION255, bookRequest.option255);
        bookQueryMap.add(OPTION256, bookRequest.option256);
        bookQueryMap.add(OPTION257, bookRequest.option257);
        bookQueryMap.add(TripMateRequestConstants.CC_ADDRESS1, bookRequest.cc_address1);
        bookQueryMap.add(TripMateRequestConstants.CC_ADDRESS2, bookRequest.cc_address2);
        bookQueryMap.add(TripMateRequestConstants.CC_CITY, bookRequest.cc_city);
        bookQueryMap.add(TripMateRequestConstants.CC_COUNTRY, bookRequest.cc_country);
        bookQueryMap.add(TripMateRequestConstants.CC_POSTALCODE, bookRequest.cc_postalcode);
        bookQueryMap.add(TripMateRequestConstants.CC_STATE, bookRequest.cc_state);
        bookQueryMap.add(TripMateRequestConstants.CC_ZIP4, bookRequest.cc_zip4);
        bookQueryMap.add(TripMateRequestConstants.CC_EMAIL, bookRequest.cc_email);
        bookQueryMap.add(TripMateRequestConstants.CC_PHONE1, bookRequest.cc_phone1);
        bookQueryMap.add(TripMateRequestConstants.CC_FIRSTNAME, bookRequest.cc_firstname);
        bookQueryMap.add(TripMateRequestConstants.CC_LASTNAME, bookRequest.cc_lastname);
        bookQueryMap.add(TripMateRequestConstants.CC_MIDDLENAME, bookRequest.cc_middlename);
        bookQueryMap.add(TripMateRequestConstants.CC_PAYMENTTYPE, bookRequest.cc_paymenttype);
        bookQueryMap.add(TripMateRequestConstants.CC_CODE, bookRequest.cc_code);
        bookQueryMap.add(TripMateRequestConstants.CC_MONTH, bookRequest.cc_month);
        bookQueryMap.add(TripMateRequestConstants.CC_YEAR, bookRequest.cc_year);
        bookQueryMap.add(TripMateRequestConstants.CC_NUMBER, bookRequest.cc_number);

        return bookQueryMap;
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {

        log.debug("Begin to process quote request");

        QuoteResult quoteResult = new QuoteResult();
        try {
            TravelSafeQuoteRequest quoteRequest = new TravelSafeQuoteRequest();

            quoteRequest.departdate = request.getDepartDate();
            quoteRequest.returndate = request.getReturnDate();

            quoteRequest.location = apiProperties.getTravelSafe().getLoc();
            quoteRequest.uname = apiProperties.getTravelSafe().getUser();
            quoteRequest.pwd = apiProperties.getTravelSafe().getPassword();
            quoteRequest.total_travs = String.valueOf(request.getTravelers().size());
            quoteRequest.departdate = request.getDepartDate();
            quoteRequest.returndate = request.getReturnDate();
            quoteRequest.individual_dates = TripMateRequestConstants.INDIVIDUAL_DATES_VALUE; // todo
            quoteRequest.country = request.getResidentCountry().name();
            quoteRequest.state = request.getResidentState() != null ? request.getResidentState().name() : "";
            List<TripMateTraveler> travelers = quoteRequest.travelers;
            for (GenericTraveler genericTraveler : request.getTravelers()) {
                TripMateTraveler traveler = new TripMateTraveler();
                travelers.add(traveler);
                traveler.age = genericTraveler.getAge();
                traveler.tripcost = genericTraveler.getTripCost();
                traveler.departdate = request.getDepartDate();
                traveler.returndate = request.getReturnDate();
                traveler.depositdate = request.getDepositDate();
            }

            TripMateQuoteResponse quote = quote(quoteRequest);

            outerloop:
            for (TripMatePlan plan : quote.plans) {
                for (TripMatePlanResponse planResponse : plan.plansByCode) {
                    // calling per product
                    String policyCode = policyMetaCode.getCode();
                    if (!policyCode.equals(planResponse.code)) {
                        continue;
                    }
                    if (!StringUtils.isEmpty(planResponse.error)) {
                        log.error("{}-{}, {}", getVendorCode(), policyCode, planResponse.error);
                        quoteResult.getErrors().add(new Result.Error("-1", planResponse.error));
                        continue;
                    }
                    if (policyMetaService.isPolicyContainsCategory(policyMeta, request)) {
                        List<Result.Error> errors = validateQuote(request, policyMeta, policyMetaCode);
                        if (CollectionUtils.isNotEmpty(errors)) {
                            String s = StringUtils.join(errors.toArray(new Object[errors.size()]), "; ");
                            log.warn("Invalid params for quote request {}-{}: {}", getVendorCode(), policyCode, s);
                            continue;
                        }
                        try {
                            BigDecimal totalCost = getUpsaleTotalCost(request, planResponse.total_cost);
                            Product product = new Product(policyMeta, policyMetaCode, totalCost, request.getUpsaleValues());
                            quoteResult.products.add(product);
                            break outerloop;
                        } catch (NumberFormatException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return quoteResult;
    }

    private BigDecimal getUpsaleTotalCost(QuoteRequest request, BigDecimal total_cost) {
        String flightAdAndDValue = request.getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "");
        if (!flightAdAndDValue.isEmpty()) {
            total_cost = total_cost.add(BigDecimal.valueOf(flightOnlyAdAndDMap.get(flightAdAndDValue))
                    .multiply(BigDecimal.valueOf(request.getTravelers().size())));
        }
        String rentalCar = request.getUpsaleValue(CategoryCodes.RENTAL_CAR, "");
        if (!rentalCar.isEmpty()) {
            total_cost = total_cost.add(BigDecimal.valueOf(request.getRentalCarLength()).multiply(BigDecimal.valueOf(7)));
        }
        return total_cost;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {

        TravelSafeBookRequest bookRequest = new TravelSafeBookRequest();
        bookRequest.location = apiProperties.getTravelSafe().getLoc();
        bookRequest.uname = apiProperties.getTravelSafe().getUser();
        bookRequest.pwd = apiProperties.getTravelSafe().getPassword();
        bookRequest.productID = request.getProduct().getPolicyMetaCode().getCode();
        bookRequest.traveler_count = String.valueOf(request.getTravelers().size());
        bookRequest.individual_dates = TripMateRequestConstants.INDIVIDUAL_DATES_VALUE; // todo

        int tripLength = (int) request.getQuoteRequest().getTripLength();
        for (GenericTraveler genericTraveler : request.getTravelers()) {
            TripMateTraveler traveler = new TripMateTraveler();

            // traveler.prefix = TripMateRequestConstants.PREFIX_VALUE; // not required
            traveler.firstname = genericTraveler.getFirstName();
            traveler.lastname = genericTraveler.getLastName();
            traveler.middlename = genericTraveler.getMiddleInitials();
            // traveler.suffix = TripMateRequestConstants.SUFFIX_VALUE; // not required
            traveler.age = genericTraveler.getAge();
            traveler.dob = "";
            // traveler.gender = TripMateRequestConstants.GENDER_VALUE; // not required
            traveler.tripcost = genericTraveler.getTripCost();
            traveler.triplength = tripLength;
            traveler.departdate = request.getQuoteRequest().getDepartDate();
            traveler.returndate = request.getQuoteRequest().getReturnDate();
            traveler.depositdate = request.getQuoteRequest().getDepositDate() != null ? request.getQuoteRequest().getDepositDate() : null;

            bookRequest.travelers.add(traveler);
        }

        bookRequest.booking_number = "";
        GenericTraveler traveler = request.getTravelers().get(0);
        bookRequest.beneficiary = traveler.getBeneficiary();
        bookRequest.tripcost = request.getQuoteRequest().getTripCost().toString();
        bookRequest.email = request.getEmail();
        bookRequest.fax = "";
        bookRequest.phone1 = request.getPhone();
        bookRequest.address1 = request.getAddress();
        bookRequest.address2 = "";
        bookRequest.state = request.getQuoteRequest().getResidentState() != null ? request.getQuoteRequest().getResidentState().name() : null;
        bookRequest.zip = request.getCreditCard().getCcZipCode();
        bookRequest.zip4 = "";
        bookRequest.city = request.getCity();

        bookRequest.country = request.getQuoteRequest().getResidentCountry() == CountryCode.US ? TripMateRequestConstants.COUNTRY_US : TripMateRequestConstants.COUNTRY_CA; // country of residence:1 - United States,2 - Canada
        bookRequest.departdate = request.getQuoteRequest().getDepartDate();
        bookRequest.returndate = request.getQuoteRequest().getReturnDate();
        bookRequest.trip_length = String.valueOf(tripLength);
        bookRequest.depositdate = request.getQuoteRequest().getDepositDate();
        bookRequest.premium = String.valueOf(request.getProduct().getTotalPrice().intValue());
        bookRequest.destination = String.valueOf(DestinationCodes.getDestinationByCode(request.getQuoteRequest().getDestinationCountry().name()));
        bookRequest.triptype = TripMateClient.getTripType(request.getTripTypes());
        bookRequest.airline = TripMateRequestConstants.AIRLINE_VALUE;
        setUpsalePurchaseParameters(bookRequest, request);
        bookRequest.cc_address1 = request.getCreditCard().getCcAddress();
        bookRequest.cc_address2 = "";
        bookRequest.cc_city = request.getCreditCard().getCcCity();
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        if (request.getCreditCard().getCcCountry() != CountryCode.US && request.getCreditCard().getCcCountry() != CountryCode.CA) {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error("-1", "Purchase error. Credit cards are only available for USA and Canada!"));
            return purchaseResponse;
        }
        bookRequest.cc_country = request.getCreditCard().getCcCountry() == CountryCode.US ? TripMateRequestConstants.COUNTRY_US : TripMateRequestConstants.COUNTRY_CA; // 1 - United States,2 - Canada

        bookRequest.cc_postalcode = request.getCreditCard().getCcZipCode();
        bookRequest.cc_state = request.getCreditCard().getCcStateCode() != null ? request.getCreditCard().getCcStateCode().name() : null;
        bookRequest.cc_zip4 = "";
        bookRequest.cc_email = request.getEmail();
        bookRequest.cc_phone1 = request.getPhone();
        bookRequest.cc_firstname = request.getCreditCard().getCcName();
        bookRequest.cc_lastname = " ";
        bookRequest.cc_middlename = "";

        String cardType;
        switch (request.getCreditCard().getCcType()) {
            case VISA:
                cardType = "v";
                break;
            case MasterCard:
                cardType = "mc";
                break;
            case AmericanExpress:
                cardType = "ae";
                break;
            case Discover:
                cardType = "d";
                break;
            default:
                cardType = "";
        }

        bookRequest.cc_paymenttype = cardType;
        bookRequest.cc_code = request.getCreditCard().getCcCode();
        bookRequest.cc_month = request.getCreditCard().getCcExpMonth();
        bookRequest.cc_year = request.getCreditCard().getCcExpYear().substring(2);
        bookRequest.cc_number = request.getCreditCard().getCcNumber().toString();

        try {
            TripMateBookResponse bookResponse = book(bookRequest);
            if (CollectionUtils.isEmpty(bookResponse.getErrors())) {
                purchaseResponse.setPolicyNumber(bookResponse.getPaymentId());
                purchaseResponse.setStatus(Result.Status.SUCCESS);
            } else {
                purchaseResponse.setStatus(Result.Status.ERROR);
                for (TripMateError error : bookResponse.getErrors()) {
                    purchaseResponse.getErrors().add(new Result.Error(error.getErrorCode(), error.getErrorText()));
                }
            }
        } catch (IOException e) {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error("-1", e.getMessage()));
            log.error(e.getMessage(), e);
            return purchaseResponse;
        }

        return purchaseResponse;
    }

    private void setUpsalePurchaseParameters(TravelSafeBookRequest bookRequest, PurchaseRequest purchaseRequest) {
        String flightAdAndDValue = purchaseRequest.getQuoteRequest().getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "");
        String policyName = purchaseRequest.getProduct().getPolicyMetaCode().getCode();
        if (!flightAdAndDValue.isEmpty()) {
            String flightAdAndDApiValue = policyMetaCategoryValueService.getApiValue(
                    purchaseRequest.getProduct().getPolicyMeta().getId(), CategoryCodes.FLIGHT_ONLY_AD_AND_D, flightAdAndDValue, purchaseRequest.getQuoteRequest());
            switch (policyName) {
                case TravelSafeClient.BASIC_PLAN_CODE:
                    bookRequest.option250 = flightAdAndDApiValue;
                    break;
                case TravelSafeClient.CLASSIC_PLAN_CODE:
                    bookRequest.option253 = flightAdAndDApiValue;
                    break;
                case TravelSafeClient.CLASSIC_PLAN_PLUS_CODE:
                    bookRequest.option256 = flightAdAndDApiValue;
                    break;
                default:
                    break;
            }
        }
        String rentalCarValue = purchaseRequest.getQuoteRequest().getUpsaleValue(CategoryCodes.RENTAL_CAR, "");
        if (!rentalCarValue.isEmpty()) {
            String rentalCarApiValue = policyMetaCategoryValueService.getApiValue(
                    purchaseRequest.getProduct().getPolicyMeta().getId(), CategoryCodes.RENTAL_CAR, rentalCarValue, purchaseRequest.getQuoteRequest());
            switch (policyName) {
                case TravelSafeClient.BASIC_PLAN_CODE:
                    bookRequest.option249 = rentalCarApiValue;
                    break;
                case TravelSafeClient.CLASSIC_PLAN_CODE:
                    bookRequest.option252 = rentalCarApiValue;
                    break;
                case TravelSafeClient.CLASSIC_PLAN_PLUS_CODE:
                    bookRequest.option255 = rentalCarApiValue;
                    break;
            }
            bookRequest.rentalcar = String.valueOf(purchaseRequest.getQuoteRequest().getRentalCarLength());
        }
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.TravelSafe;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        return Collections.emptyList();
    }
}
