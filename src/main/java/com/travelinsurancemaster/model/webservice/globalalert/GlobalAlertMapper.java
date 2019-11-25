package com.travelinsurancemaster.model.webservice.globalalert;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.webservice.common.CardType;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.PolicyMetaCategoryValueService;
import com.travelinsurancemaster.services.clients.TripMateClient;
import com.travelinsurancemaster.services.tripmate.DestinationCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maleev on 01.06.2016.
 */
@Component
public class GlobalAlertMapper {

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    private static final String ESSENTIALS_PLAN_CODE = "1003";
    private static final String PREFERRED_PLAN_CODE = "1004";
    private static final String PREFERRED_PLUS_PLAN_CODE = "1005";

    public static final String LOCATION_PARAM_NAME = "location";
    public static final String LOGIN_PARAM_NAME = "uname";
    public static final String PRODUCT_ID_PARAM_NAME = "productID";
    public static final String TRAVELERS_COUNT_PARAM_NAME = "traveler_count";
    public static final String PASSWORD_PARAM_NAME = "pwd";
    public static final String TRAVELER_AGE_PARAM_NAME = "age";
    public static final String TRAVELER_TRIPCOST_PARAM_NAME = "tripcost";
    public static final String STATE_PARAM_NAME = "state";
    public static final String COUNTRY_PARAM_NAME = "country";
    public static final String DEPARTDATE_PARAM_NAME = "departdate";
    public static final String RETURNDATE_PARAM_NAME = "returndate";
    public static final String DEPOSITE_DATE_PARAM_NAME = "depositdate";
    public static final String INDIVIDUAL_DATES_PARAM_NAME = "individual_dates";


    public static final String TRAVELER_FIRSTNAME_PARAM_NAME = "firstname";
    public static final String TRAVELER_LASTNAME_PARAM_NAME = "lastname";
    public static final String TRAVELER_STATE_PARAM_NAME = "state";
    public static final String BENEFICIARY_PARAM_NAME = "beneficiary";
    public static final String TRIPCOST_PARAM_NAME = "tripcost";
    public static final String EMAIL_PARAM_NAME = "email";
    public static final String PHONE_PARAM_NAME = "phone1";
    public static final String ADDRESS1_PARAM_NAME = "address1";
    public static final String ADDRESS2_PARAM_NAME = "address2";
    public static final String ZIP_PARAM_NAME = "zip";
    public static final String CITY_PARAM_NAME = "city";

    public static final String DESTINATION_PARAM_NAME = "destination";
    public static final String TRIP_TYPE_PARAM_NAME = "triptype";

    public static final String CREDITCARD_ADDRESS1_PARAM_NAME = "cc_address1";
    public static final String CREDITCARD_ADDRESS2_PARAM_NAME = "cc_address2";
    public static final String CREDITCARD_CITY_PARAM_NAME = "cc_city";
    public static final String CREDITCARD_COUNTRY_PARAM_NAME = "cc_country";
    public static final String CREDITCARD_POSTCODE_PARAM_NAME = "cc_postalcode";
    public static final String CREDITCARD_STATE_PARAM_NAME = "cc_state";
    public static final String CREDITCARD_EMAIL_PARAM_NAME = "cc_email";
    public static final String CREDITCARD_PHONE_PARAM_NAME = "cc_phone";
    public static final String CREDITCARD_FIRSTNAME_PARAM_NAME = "cc_firstname";
    public static final String CREDITCARD_LASTNAME_PARAM_NAME = "cc_lastname";
    public static final String CREDITCARD_PAYMENT_TYPE_PARAM_NAME = "cc_paymenttype";
    public static final String CREDITCARD_CODE_PARAM_NAME = "cc_code";
    public static final String CREDITCARD_EXPIRATION_MONTH_PARAM_NAME = "cc_month";
    public static final String CREDITCARD_EXPIRATION_YEAR_PARAM_NAME = "cc_year";
    public static final String CREDITCARD_NUMBER_PARAM_NAME = "cc_number";
    public static final String PREMIUM_PARAM_NAME = "premium";

    public static final String AIRLIME_PARAM_NAME = "airline";
    public static final String OTHER_AIRLIME_PARAM_VALUE = "567";
    public static final String CRUISELINE_PARAM_NAME = "cruiseline";
    public static final String OTHER_CRUISELINE_PARAM_VALUE = "529";
    public static final String MISCPROV_PARAM_NAME = "miscprov";
    public static final String OTHER_MISCPROV_PARAM_VALUE = "519";



    public static final DateTimeFormatter commonDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static List<CountryCode> restrictedCountries = new ArrayList<>();

    private static HashMap<String, String> upsalesParamsNames = new HashMap<>();

    static {
        restrictedCountries.add(CountryCode.AF);
        restrictedCountries.add(CountryCode.BI);
        restrictedCountries.add(CountryCode.CF);
        restrictedCountries.add(CountryCode.TD);
        restrictedCountries.add(CountryCode.CG);
        restrictedCountries.add(CountryCode.ER);
        restrictedCountries.add(CountryCode.GN);
        restrictedCountries.add(CountryCode.IR);
        restrictedCountries.add(CountryCode.IQ);
        restrictedCountries.add(CountryCode.LB);
        restrictedCountries.add(CountryCode.LR);
        restrictedCountries.add(CountryCode.LY);
        restrictedCountries.add(CountryCode.ML);
        restrictedCountries.add(CountryCode.NE);
        restrictedCountries.add(CountryCode.KP);
        restrictedCountries.add(CountryCode.PK);
        restrictedCountries.add(CountryCode.PS);
        restrictedCountries.add(CountryCode.SL);
        restrictedCountries.add(CountryCode.SO);
        restrictedCountries.add(CountryCode.SD);
        restrictedCountries.add(CountryCode.SY);
        restrictedCountries.add(CountryCode.YE);

        upsalesParamsNames.put(ESSENTIALS_PLAN_CODE + CategoryCodes.TWENTY_FOUR_HOUR_AD_AND_D, "option400");
        upsalesParamsNames.put(ESSENTIALS_PLAN_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, "option401");
        upsalesParamsNames.put(PREFERRED_PLAN_CODE + CategoryCodes.TWENTY_FOUR_HOUR_AD_AND_D, "option402");
        upsalesParamsNames.put(PREFERRED_PLAN_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, "option403");
        upsalesParamsNames.put(PREFERRED_PLUS_PLAN_CODE + CategoryCodes.TWENTY_FOUR_HOUR_AD_AND_D, "option404");
        upsalesParamsNames.put(PREFERRED_PLUS_PLAN_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, "option405");
        upsalesParamsNames.put(PREFERRED_PLUS_PLAN_CODE + CategoryCodes.RENTAL_CAR, "option406");

    }

    public  MultiValueMap<String, String> RequestToMap(QuoteRequest quoteRequest, PolicyMeta policyMeta, String policyMetaCode, InsuranceMasterApiProperties.GlobalAlert globalAlertProperties) {
        MultiValueMap<String, String> quoteParams = new LinkedMultiValueMap<>();
        quoteParams.add(LOCATION_PARAM_NAME, globalAlertProperties.getLocation());
        quoteParams.add(LOGIN_PARAM_NAME, globalAlertProperties.getUser());
        quoteParams.add(PASSWORD_PARAM_NAME, globalAlertProperties.getPassword());
        quoteParams.add(TRAVELERS_COUNT_PARAM_NAME, String.valueOf(quoteRequest.getTravelers().size()));
        quoteParams.add(PRODUCT_ID_PARAM_NAME, policyMetaCode);

        quoteParams.add(STATE_PARAM_NAME, quoteRequest.getResidentState().toString());
        quoteParams.add(COUNTRY_PARAM_NAME, getGAResidenceCoutryCode(quoteRequest.getCitizenCountry()));
        quoteParams.add(DEPARTDATE_PARAM_NAME, commonDateFormat.format(quoteRequest.getDepartDate()));
        quoteParams.add(RETURNDATE_PARAM_NAME, commonDateFormat.format(quoteRequest.getReturnDate()));

        if (quoteRequest.getDepositDate() != null) {
            quoteParams.add(DEPOSITE_DATE_PARAM_NAME, commonDateFormat.format(quoteRequest.getDepositDate()));
        }

        for(int i = 0; i < quoteRequest.getTravelers().size(); i++){
            String travelerPrefix = "t" + (i+1) + "_";
            quoteParams.add(travelerPrefix + TRAVELER_AGE_PARAM_NAME,  String.valueOf(quoteRequest.getTravelers().get(i).getAge()));
            quoteParams.add(travelerPrefix + TRAVELER_TRIPCOST_PARAM_NAME,  String.valueOf(quoteRequest.getTravelers().get(i).getTripCost()));
        }

        quoteParams.add(INDIVIDUAL_DATES_PARAM_NAME, "0");

        processUpsales(quoteParams,policyMeta, policyMetaCode, quoteRequest);

        return quoteParams;
    }

    public  MultiValueMap<String, String> purchaseToMap(PurchaseRequest request, InsuranceMasterApiProperties.GlobalAlert globalAlertProperties){
        MultiValueMap<String, String> purchaseParams = RequestToMap(request.getQuoteRequest(), request.getProduct().getPolicyMeta(), request.getProduct().getPolicyMetaCode().getCode(), globalAlertProperties);

        for(int i = 0; i < request.getTravelers().size(); i++){
            String travelerPrefix = "t" + (i+1) + "_";
            purchaseParams.add(travelerPrefix + TRAVELER_FIRSTNAME_PARAM_NAME, request.getTravelers().get(i).getFirstName());
            purchaseParams.add(travelerPrefix + TRAVELER_LASTNAME_PARAM_NAME, request.getTravelers().get(i).getLastName());
            purchaseParams.add(travelerPrefix + TRAVELER_STATE_PARAM_NAME, request.getQuoteRequest().getResidentState().toString());
        }

        purchaseParams.add(BENEFICIARY_PARAM_NAME, request.getTravelers().get(0).getBeneficiary());
        purchaseParams.add(TRIPCOST_PARAM_NAME, request.getQuoteRequest().getTripCost().toString());
        purchaseParams.add(EMAIL_PARAM_NAME, request.getEmail());
        purchaseParams.add(PHONE_PARAM_NAME, request.getPhone());


        purchaseParams.add(DESTINATION_PARAM_NAME, String.valueOf(DestinationCodes.getDestinationByCode(request.getQuoteRequest().getDestinationCountry().toString())));


        purchaseParams.add(TRIP_TYPE_PARAM_NAME, TripMateClient.getTripType(request.getTripTypes()));


        //address
        purchaseParams.add(ADDRESS1_PARAM_NAME, request.getAddress());
        purchaseParams.add(ZIP_PARAM_NAME, request.getPostalCode());
        purchaseParams.add(CITY_PARAM_NAME, request.getCity());
        purchaseParams.add(PREMIUM_PARAM_NAME, request.getProduct().getTotalPrice().toString());

        setOneCarrierCompany(request,purchaseParams);
        //Credit card
        purchaseParams.add(CREDITCARD_ADDRESS1_PARAM_NAME, request.getCreditCard().getCcAddress());
        purchaseParams.add(CREDITCARD_CITY_PARAM_NAME, request.getCreditCard().getCcCity());
        purchaseParams.add(CREDITCARD_COUNTRY_PARAM_NAME, getGAResidenceCoutryCode(request.getCreditCard().getCcCountry()));
        purchaseParams.add(CREDITCARD_PAYMENT_TYPE_PARAM_NAME, getPaymentType(request.getCreditCard().getCcType()));
        purchaseParams.add(CREDITCARD_POSTCODE_PARAM_NAME, request.getCreditCard().getCcZipCode());
        purchaseParams.add(CREDITCARD_STATE_PARAM_NAME, request.getCreditCard().getCcStateCode().toString());
        purchaseParams.add(CREDITCARD_EMAIL_PARAM_NAME, request.getEmail());
        purchaseParams.add(CREDITCARD_PHONE_PARAM_NAME, request.getPhone());

        String[] names = request.getCreditCard().getCcName().split(" ",2);

        purchaseParams.add(CREDITCARD_FIRSTNAME_PARAM_NAME, names[0]);
        if (names.length > 1) {
            purchaseParams.add(CREDITCARD_LASTNAME_PARAM_NAME, names[1]);
        }

        purchaseParams.add(CREDITCARD_CODE_PARAM_NAME, request.getCreditCard().getCcCode());
        purchaseParams.add(CREDITCARD_EXPIRATION_MONTH_PARAM_NAME,request.getCreditCard().getCcExpMonth());
        purchaseParams.add(CREDITCARD_EXPIRATION_YEAR_PARAM_NAME, request.getCreditCard().getCcExpYear().substring(2));
        purchaseParams.add(CREDITCARD_NUMBER_PARAM_NAME, request.getCreditCard().getCcNumber().toString());

        return purchaseParams;

    }

    private  void processUpsales(MultiValueMap<String, String> quoteParams, PolicyMeta policyMeta, String policyMetaCode, QuoteRequest quoteRequest){
        if (quoteRequest.getUpsaleValues().size() == 0) return;

        for(String categoryCode: quoteRequest.getUpsaleValues().keySet()) {
            String upsaleValue = quoteRequest.getUpsaleValues().get(categoryCode);
            String apiValue = policyMetaCategoryValueService.getApiValue(policyMeta.getId(), categoryCode, upsaleValue, quoteRequest);
            String optionParamName = upsalesParamsNames.get(policyMetaCode + categoryCode);

            if (!StringUtils.isEmpty(apiValue)){
                quoteParams.add(optionParamName,apiValue);
            }
        }
    }

    private static String getGAResidenceCoutryCode(CountryCode countryCode){
        return countryCode.equals(CountryCode.US) ? "1": "2";
    }

    private static String getPaymentType(CardType creditType){
        switch (creditType) {
            case VISA:
                return "v";
            case MasterCard:
                return "mc";
            case AmericanExpress:
                return "ae";
            case Discover:
                return "d";
            default:
                return "";
        }
    }


    private static void setOneCarrierCompany(PurchaseRequest request,MultiValueMap<String, String> purchaseParams){
        if (request.getTripTypes().contains("Air")){
            purchaseParams.add(AIRLIME_PARAM_NAME, OTHER_AIRLIME_PARAM_VALUE);
        }
        else if (request.getTripTypes().contains("Cruise")){
            purchaseParams.add(CRUISELINE_PARAM_NAME, OTHER_CRUISELINE_PARAM_VALUE);
        } else {
            purchaseParams.add(MISCPROV_PARAM_NAME, OTHER_MISCPROV_PARAM_VALUE);
        }

    }
}
