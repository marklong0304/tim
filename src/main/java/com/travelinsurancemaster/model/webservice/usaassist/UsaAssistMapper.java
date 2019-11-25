package com.travelinsurancemaster.model.webservice.usaassist;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.Product;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;

/**
 * Created by ritchie on 10/1/15.
 */
public class UsaAssistMapper {

    public static final String AFFILIATE = "affiliate";
    public static final String PASSWORD = "password";
    public static final String TEST = "test";
    public static final String LANG = "lang";
    public static final String PLAN_CODE = "plan_code";
    public static final String CANCELLATION = "cancellation";
    public static final String COINSURANCE = "coinsurance";
    public static final String FLIGHT_COVERAGE = "flight_coverage";
    public static final String COUNTRY_CODE = "country_code";
    public static final String STATE = "state";
    public static final String CITY = "city";
    public static final String ADDRESS = "address";
    public static final String ZIP_CODE = "zip_code";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String DESTINATION_CODE = "destination_code";
    public static final String START = "start";
    public static final String END = "end";
    public static final String OPERATOR = "operator";
    public static final String TRAVELERS = "travelers";
    public static final String TRIP_COST = "trip_cost";
    public static final String CARD_NUM = "card_num";
    public static final String CARD_EXP = "card_exp";
    public static final String CARD_CODE = "card_code";
    public static final String CARD_FNAME = "card_fname";
    public static final String CARD_LNAME = "card_lname";
    public static final String CARD_ZIP = "card_zip";
    public static final String CARD_COUNTRY = "card_country";
    public static final String CUSTOMER_IP = "customer_ip";
    public static final String TOTAL = "total";
    public static final String LAST_NAME = "last_name";
    public static final String FIRST_NAME = "first_name";
    public static final String DOB = "dob";
    public static final String PASSPORT = "passport";
    public static final String RELATIONSHIP = "relationship";
    public static final String TRAVELING = "traveling";
    public static final String AGES = "ages";

    public static final String CANCELLATION_PROTECTION = "C";
    public static final String RELATIONSHIP_PRIMARY = "1";
    public static final String RELATIONSHIP_ANY_DEPENDENT = "4";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static MultiValueMap<String, String> toMap(QuoteRequest quoteRequest, String policyCode, InsuranceMasterApiProperties.UsaAssist usaAssistProperties) {
        MultiValueMap<String, String> quoteParams = new LinkedMultiValueMap<>();
        quoteParams.add(AFFILIATE, usaAssistProperties.getUser());
        quoteParams.add(PASSWORD, usaAssistProperties.getPassword());
        quoteParams.add(COUNTRY_CODE, quoteRequest.getResidentCountry().name());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        quoteParams.add(START, dateFormat.format(quoteRequest.getDepartDate()));
        quoteParams.add(END, dateFormat.format(quoteRequest.getReturnDate()));
        quoteParams.add(DESTINATION_CODE, quoteRequest.getDestinationCountry().name());
        quoteParams.add(TRAVELERS, String.valueOf(quoteRequest.getTravelers().size()));
        quoteParams.add(COINSURANCE, "");
        addUpsale(quoteRequest, quoteParams);
        quoteParams.add(PLAN_CODE, policyCode);
        quoteParams.add(TRAVELING, "");
        for (GenericTraveler traveler : quoteRequest.getTravelers()) {
            quoteParams.add(AGES, String.valueOf(traveler.getAge()));
        }
        quoteParams.add(TRIP_COST, String.valueOf(quoteRequest.getTripCost()));
        return quoteParams;
    }

    private static void addUpsale(QuoteRequest quoteRequest, MultiValueMap<String, String> params) {
        String flightOnlyAdAndDValue = quoteRequest.getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "");
        params.add(FLIGHT_COVERAGE, flightOnlyAdAndDValue);
    }

    public static MultiValueMap<String, String> toMap(PurchaseRequest purchaseRequest, InsuranceMasterApiProperties.UsaAssist usaAssist) {
        MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
        purchaseParams.add(AFFILIATE, usaAssist.getUser());
        purchaseParams.add(PASSWORD, usaAssist.getPassword());
        purchaseParams.add(TEST, "true");
        purchaseParams.add(LANG, "");
        Product product = purchaseRequest.getProduct();
        purchaseParams.add(PLAN_CODE, product.getPolicyMetaCode().getCode());
        purchaseParams.add(CANCELLATION, ""); // todo: for Cancellation set 'C' -  Only Available for Annual plans
        purchaseParams.add(COINSURANCE, "");
        addUpsale(purchaseRequest.getQuoteRequest(), purchaseParams);
        QuoteRequest quoteRequest = purchaseRequest.getQuoteRequest();
        purchaseParams.add(COUNTRY_CODE, quoteRequest.getResidentCountry().name());
        purchaseParams.add(STATE, quoteRequest.getResidentState() != null ? quoteRequest.getResidentState().name() : "");
        purchaseParams.add(CITY, purchaseRequest.getCity());
        purchaseParams.add(ADDRESS, purchaseRequest.getAddress());
        purchaseParams.add(ZIP_CODE, purchaseRequest.getPostalCode());
        purchaseParams.add(PHONE, purchaseRequest.getPhone());
        purchaseParams.add(EMAIL, purchaseRequest.getEmail());
        purchaseParams.add(DESTINATION_CODE, quoteRequest.getDestinationCountry().name());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        purchaseParams.add(START, dateFormat.format(quoteRequest.getDepartDate()));
        purchaseParams.add(END, dateFormat.format(quoteRequest.getReturnDate()));
        purchaseParams.add(OPERATOR, "");
        purchaseParams.add(TRAVELERS, String.valueOf(purchaseRequest.getTravelers().size()));
        purchaseParams.add(TRIP_COST, String.valueOf(purchaseRequest.getTravelers().get(0).getTripCost())); // TRIP_COST is per traveler.
        CreditCard creditCard = purchaseRequest.getCreditCard();
        purchaseParams.add(CARD_NUM, String.valueOf(creditCard.getCcNumber()));
        String expirationDate = creditCard.getCcExpMonth() + "/" + creditCard.getCcExpYear();
        purchaseParams.add(CARD_EXP, expirationDate);
        purchaseParams.add(CARD_CODE, creditCard.getCcCode());
        purchaseParams.add(CARD_FNAME, creditCard.getCcName());
        purchaseParams.add(CARD_LNAME, "");
        purchaseParams.add(CARD_ZIP, creditCard.getCcZipCode());
        purchaseParams.add(CARD_COUNTRY, creditCard.getCcCountry().name());
        purchaseParams.add(CUSTOMER_IP, "");
        purchaseParams.add(TOTAL, String.valueOf(product.getTotalPrice()));
        for (GenericTraveler traveler : purchaseRequest.getTravelers()) {
            purchaseParams.add(FIRST_NAME, traveler.getFirstName());
            purchaseParams.add(LAST_NAME, traveler.getLastName());
            purchaseParams.add(DOB, dateFormat.format(traveler.getBirthdaySafe()));
            purchaseParams.add(RELATIONSHIP, traveler.isPrimary() ? RELATIONSHIP_PRIMARY : RELATIONSHIP_ANY_DEPENDENT);
        }
        purchaseParams.add(PASSPORT, "");
        return purchaseParams;
    }
}
