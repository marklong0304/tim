package com.travelinsurancemaster.model.webservice.travelexinsurance;

import com.travelinsurancemaster.clients.travelex.GetOnusTokenResponse;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.PolicyMetaCategoryService;
import com.travelinsurancemaster.services.PolicyMetaCategoryValueService;
import com.travelinsurancemaster.services.clients.TravelexInsuranceClient;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by ritchie on 4/27/15.
 */
@Component
public class TravelexInsuranceMapper {

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    public static final String COVERAGE_TRIP_CANCELLATION = "TC";
    public static final String COVERAGE_NO_TRIP_CANCELLATION = "NTC";
    public static final String COVERAGE_FLIGHT_ONLY = "FO";
    public static final String COVERAGE_PACKAGE_PLAN = "PK";
    public static final String COVERAGE_CANCEL_FOR_ANY_REASON = "CFR";
    public static final String COVERAGE_RENTAL_CAR = "RVD";
    public static final String COVERAGE_TRANSPORT_PAK = "TRANSPAK";
    public static final String COVERAGE_FLIGHT_AD_D = "ADD";
    public static final String COVERAGE_SPORTS = "ADVS";

    public static final String STR_DEPARTURE_DATE = "strDepartureDate";
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    public static final String STR_RETURN_DATE = "strReturnDate";
    public static final String STR_PRODUCT = "strProduct";
    public static final String STR_FORM = "strForm";
    public static final String STR_COVERAGE_TYPE = "strCoverageType";
    public static final String STR_LOCATION = "strLocation";
    public static final String INT_FAC_PREMIUM = "intFacPremium";
    public static final String INT_NUM_TRAVELERS = "intNumTravelers";
    public static final String STR_DOB = "strDOB";
    public static final String INT_TRIP_COST = "intTripCost";
    public static final String STR_MED_UPGRADE = "strMedUpgrade";
    public static final String STR_STATE = "strState";
    public static final String STR_COUNTRY = "strCountry";
    public static final String STR_COLLISION_START_DATE = "strCollisionStartDate";
    public static final String STR_COLLISION_END_DATE = "strCollisionEndDate";
    public static final String STR_REFERENCE_ID = "strReferenceID";
    private static final String COVERAGE_TYPE_DELIMITER = ",";
    public static final String STR_USER_ID = "strUserID";
    public static final String STR_PASSWORD = "strPassword";
    public static final String STR_BROKER_LOCATION = "strBrokerLocation";
    public static final String STR_AGENCY_LOCATION = "strAgencyLocation";
    public static final String STR_AGENT_CODE = "strAgentCode";
    public static final String STR_GROUP_ID = "strGroupID";
    public static final String STR_INVOICE_NUMBER = "strInvoiceNumber";
    public static final String DBL_TOTAL_POLICY_COST = "dblTotalPolicyCost";
    public static final String STR_PURCHASE_DATE = "strPurchaseDate";
    public static final String STR_FIRST_NAME = "strFirstName";
    public static final String STR_LAST_NAME = "strLastName";
    public static final String STR_ADDRESS_1 = "strAddress1";
    public static final String STR_ADDRESS_2 = "strAddress2";
    public static final String STR_CITY = "strCity";
    public static final String STR_ZIP = "strZip";
    public static final String STR_PHONE = "strPhone";
    public static final String STR_FAX = "strFax";
    public static final String STR_EMAIL = "strEmail";
    public static final String STR_BENEFICIARY = "strBeneficiary";
    public static final String STR_PAYMENT_TYPE = "strPaymentType";
    public static final String PAYMENT_TYPE_VISA = "VI";
    public static final String TOKEN_CC_NUMBER = "tokenCCNumber";
    public static final String STR_MASKED_CARD_NUMBER = "strMaskedCardNumber";
    public static final String STR_CC_CARD_HOLDER_NAME = "strCCCardHolderName";
    public static final String STR_CC_EXPIRATION_MONTH = "strCCExpirationMonth";
    public static final String STR_CC_EXPIRATION_YEAR = "strCCExpirationYear";
    public static final String STR_CC_AUTHORIZATION_NUMBER = "strCCAuthorizationNumber";
    public static final String STR_EXTERNALLY_AUTHORIZED = "strExternallyAuthorized";
    public static final String STR_CHECK_NUMBER = "strCheckNumber";
    public static final String STR_CRUISE_LINE = "strCruiseLine";
    public static final String STR_TOUR_OPERATOR = "strTourOperator";
    public static final String STR_AIRLINE = "strAirLine";
    public static final String STR_DESTINATION = "strDestination";
    public static final String STR_FLIGHT_NUMBER = "strFlightNumber";
    private static final String MEDICAL_UPGRADE = "MU";

    public static final String LOCATION_NUMBER = "LocationNumber";
    public static final String USER_ID = "UserID";
    public static final String PASSWORD = "Password";
    public static final String PRODUCT_FORM_NUMBER = "ProductFormNumber";
    public static final String HOST_URL = "HostUrl";

    public MultiValueMap<String, String> toMap(QuoteRequest request, PolicyMetaCode policyMetaCode, InsuranceMasterApiProperties.TravelexInsurance travelexInsuranceProperties, PolicyMeta policyMeta) {
        TravelexUpsaleContext context = new TravelexUpsaleContext();
        MultiValueMap<String, String> quoteParams = new LinkedMultiValueMap<>();
        quoteParams.add(STR_LOCATION, travelexInsuranceProperties.getLocationNumber());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        quoteParams.add(STR_DEPARTURE_DATE, dateFormat.format(request.getDepartDate()));
        quoteParams.add(STR_RETURN_DATE, dateFormat.format(request.getReturnDate()));
        String policyCode = policyMetaCode.getCode();
        quoteParams.add(STR_PRODUCT, policyCode);
//        quoteParams.add(STR_FORM, ""); // todo: optional? what version number?
        addUpsaleParameters(request, policyMetaCode.getCode(), policyMetaCode.getPolicyMeta().getId(), quoteParams, context);
        quoteParams.add(INT_NUM_TRAVELERS, String.valueOf(request.getTravelers().size()));

        boolean limited = request.getPlanType().getId() == PlanType.LIMITED.getId();

        addTravelers(quoteParams, request.getTravelers(), policyCode, limited, policyMeta);

        quoteParams.add(STR_STATE, Objects.toString(request.getResidentState()));
        quoteParams.add(STR_COUNTRY, WordUtils.capitalizeFully(request.getResidentCountry().getCaption()));

        if(!context.isCarRental()) {
            quoteParams.add(STR_COLLISION_START_DATE, "");
            quoteParams.add(STR_COLLISION_END_DATE, "");
        }

        quoteParams.add(STR_REFERENCE_ID, ""); // todo
        quoteParams.add(STR_FORM, "");
        return quoteParams;
    }

    public MultiValueMap<String, String> toMap(PurchaseRequest request, InsuranceMasterApiProperties.TravelexInsurance travelexInsuranceProperties, PolicyMeta policyMeta, GetOnusTokenResponse getOnusResponse) {
        TravelexUpsaleContext context = new TravelexUpsaleContext();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
        purchaseParams.add(STR_USER_ID, travelexInsuranceProperties.getUser());
        purchaseParams.add(STR_PASSWORD, travelexInsuranceProperties.getPassword());
        purchaseParams.add(STR_BROKER_LOCATION, "");
        purchaseParams.add(STR_AGENCY_LOCATION, travelexInsuranceProperties.getLocationNumber());
        purchaseParams.add(STR_AGENT_CODE, "");
        purchaseParams.add(STR_GROUP_ID, "");
        purchaseParams.add(STR_INVOICE_NUMBER, "");
        purchaseParams.add(DBL_TOTAL_POLICY_COST, "0"); // Not applicable
        purchaseParams.add(STR_DEPARTURE_DATE, dateFormat.format(request.getQuoteRequest().getDepartDate()));
        purchaseParams.add(STR_RETURN_DATE, dateFormat.format(request.getQuoteRequest().getReturnDate()));
        purchaseParams.add(STR_PURCHASE_DATE, dateFormat.format(LocalDate.now()));
        String policyCode = request.getProduct().getPolicyMetaCode().getCode();
        purchaseParams.add(STR_PRODUCT, policyCode);
        purchaseParams.add(STR_FORM, "");
        addUpsaleParameters(request.getQuoteRequest(), request.getProduct().getPolicyMetaCode().getCode(), request.getProduct().getPolicyMeta().getId(), purchaseParams, context);
        purchaseParams.add(INT_NUM_TRAVELERS, String.valueOf(request.getTravelers().size()));

        addTravelers(purchaseParams, request.getTravelers(), policyCode, false, policyMeta);


        addNames(purchaseParams, request.getTravelers());
        purchaseParams.add(STR_ADDRESS_1, request.getAddress());
        purchaseParams.add(STR_ADDRESS_2, "");
        purchaseParams.add(STR_CITY, request.getCity());
        purchaseParams.add(STR_STATE, Objects.toString(request.getQuoteRequest().getResidentState()));
        purchaseParams.add(STR_ZIP, request.getPostalCode());
        purchaseParams.add(STR_COUNTRY, WordUtils.capitalizeFully(request.getQuoteRequest().getResidentCountry().getCaption()));
        purchaseParams.add(STR_PHONE, request.getPhone());
        purchaseParams.add(STR_FAX, "");
        purchaseParams.add(STR_EMAIL, request.getEmail());

        if(!context.isCarRental()) {
            purchaseParams.add(STR_COLLISION_START_DATE, "");
            purchaseParams.add(STR_COLLISION_END_DATE, "");
        }

        GenericTraveler traveler = request.getTravelers().get(0);
        purchaseParams.add(STR_BENEFICIARY, traveler.getBeneficiary());
        String paymentType;
        switch (request.getCreditCard().getCcType()) {
            case VISA:
                paymentType = "VI";
                break;
            case MasterCard:
                paymentType = "MC";
                break;
            case Discover:
                paymentType = "DS";
                break;
            case AmericanExpress:
                paymentType = "AM";
                break;
            default:
                paymentType = "";
        }
        purchaseParams.add(STR_PAYMENT_TYPE, paymentType);
        purchaseParams.add(TOKEN_CC_NUMBER, getOnusResponse.getToken());
        purchaseParams.add(STR_MASKED_CARD_NUMBER, getOnusResponse.getMaskedCardNumber());
        purchaseParams.add(STR_CC_CARD_HOLDER_NAME, request.getCreditCard().getCcName());
        purchaseParams.add(STR_CC_EXPIRATION_MONTH, request.getCreditCard().getCcExpMonth());
        purchaseParams.add(STR_CC_EXPIRATION_YEAR, request.getCreditCard().getCcExpYear());
        purchaseParams.add(STR_CC_AUTHORIZATION_NUMBER, getOnusResponse.getTransactionID());
        purchaseParams.add(STR_EXTERNALLY_AUTHORIZED, "");
        purchaseParams.add(STR_CHECK_NUMBER, "");
        purchaseParams.add(STR_CRUISE_LINE, "");
        purchaseParams.add(STR_TOUR_OPERATOR, "");
        purchaseParams.add(STR_AIRLINE, "");
        purchaseParams.add(STR_DESTINATION, request.getQuoteRequest().getDestinationCountry().name());
        purchaseParams.add(STR_FLIGHT_NUMBER, "");
        return purchaseParams;
    }

    private void addUpsaleParameters(QuoteRequest request, String code, Long policyMetaId, MultiValueMap<String, String> params, TravelexUpsaleContext context) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);

        String coverageType = null;
        if (request.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            coverageType = COVERAGE_TRIP_CANCELLATION;
        } else {
            coverageType = COVERAGE_TRIP_CANCELLATION;
        }

        String rentalCarUpsale = request.getUpsaleValue(CategoryCodes.RENTAL_CAR, "");
        if (!rentalCarUpsale.isEmpty()) {
            coverageType += COVERAGE_TYPE_DELIMITER + COVERAGE_RENTAL_CAR;

            params.add(STR_COLLISION_START_DATE, dateFormat.format(request.getRentalCarStartDateSafe()));
            params.add(STR_COLLISION_END_DATE, dateFormat.format(request.getRentalCarEndDateSafe()));
            context.setCarRental(true);
        }

        final String EMERGENCY_MED_DEF_VAL = "50000";
        String emergencyMedicalValue = request.getUpsaleValue(CategoryCodes.EMERGENCY_MEDICAL, EMERGENCY_MED_DEF_VAL);
        final String MEDICAL_EVAC_DEF_VAL = "500000";
        String medicalEvacValue = request.getUpsaleValue(CategoryCodes.MEDICAL_EVACUATION, MEDICAL_EVAC_DEF_VAL);
        if (
            !emergencyMedicalValue.isEmpty() && !emergencyMedicalValue.equals(EMERGENCY_MED_DEF_VAL)
            ||
            !medicalEvacValue.isEmpty() && !medicalEvacValue.equals(MEDICAL_EVAC_DEF_VAL)
        ) {
            coverageType += COVERAGE_TYPE_DELIMITER + MEDICAL_UPGRADE;
        }

        String addUpsale = request.getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "");
        if (!addUpsale.isEmpty()) {
            coverageType += COVERAGE_TYPE_DELIMITER + COVERAGE_FLIGHT_AD_D;
        }

        String sportsUpsale = request.getUpsaleValue(CategoryCodes.HAZARDOUS_SPORTS, "");
        if (!sportsUpsale.isEmpty()) {
            coverageType += COVERAGE_TYPE_DELIMITER + COVERAGE_SPORTS;
        }

        String cancelAnyReasonUpsale = request.getUpsaleValue(CategoryCodes.CANCEL_FOR_ANY_REASON, "");
        if (!cancelAnyReasonUpsale.isEmpty()) {
            coverageType += COVERAGE_TYPE_DELIMITER + COVERAGE_CANCEL_FOR_ANY_REASON;
        }

        params.add(STR_MED_UPGRADE, "");

        String productFormNumber = getProductFormNumber(code);
        if (Objects.nonNull(productFormNumber)) {
            params.add(STR_FORM, productFormNumber);
        }

        if (
            code.equals(TravelexInsuranceClient.TRAVEL_BASIC_PRODUCT_CODE)
            ||
            code.equals(TravelexInsuranceClient.TRAVEL_SELECT_PRODUCT_CODE)
            ||
            code.equals(TravelexInsuranceClient.FLIGHT_INSURE_PRODUCT_CODE)
        ) {
            params.add(INT_FAC_PREMIUM, "0");
            String rentalCarValue = request.getUpsaleValue(CategoryCodes.RENTAL_CAR, "");
            String flightAdAndD = request.getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "");
            List<PolicyMetaCategoryValue> sortedCatVal = policyMetaCategoryValueService.getSortedCategoryValues(policyMetaId, CategoryCodes.FLIGHT_ONLY_AD_AND_D, request);
            boolean defVal = policyMetaCategoryValueService.isDefaultValue(sortedCatVal, flightAdAndD);
            params.add(STR_COVERAGE_TYPE, coverageType);
        }
    }

    private void addTravelers(MultiValueMap<String, String> params, List<GenericTraveler> travelers, String code, boolean limited, PolicyMeta policyMeta) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        for (GenericTraveler traveler : travelers) {
            if (traveler.isPrimary()) {
                params.add(STR_DOB, dateFormat.format(traveler.getBirthdaySafe()));
                if (!(code.equals(TravelexInsuranceClient.BUSINESS_TRAVELER_SINGLE_CODE) ||
                        code.equals(TravelexInsuranceClient.BUSINESS_TRAVELER_MULTI_CODE))) {
                    if(limited) {
                        if(policyMeta != null && policyMeta.hasMinimalTripCost())
                            params.add(policyMeta == null ? "tripCost" : INT_TRIP_COST, String.valueOf(policyMeta.getMinimalTripCost().intValue()));
                        else
                            params.add(policyMeta == null ? "tripCost" : INT_TRIP_COST, "1");
                    }
                    else
                        params.add(policyMeta == null ? "tripCost" : INT_TRIP_COST, String.valueOf(traveler.getTripCost().intValue()));
                }
            }
        }
        if (travelers.size() > 1) {
            for (GenericTraveler traveler : travelers) {
                if (!traveler.isPrimary()) {
                    params.add(STR_DOB, dateFormat.format(traveler.getBirthdaySafe()));
                    if(limited) {
                        if(policyMeta != null && policyMeta.hasMinimalTripCost())
                            params.add(INT_TRIP_COST, String.valueOf(policyMeta.getMinimalTripCost().intValue()));
                        else
                            params.add(policyMeta == null ? "tripCost" : INT_TRIP_COST, "1");
                    }
                    else
                        params.add(policyMeta == null ? "tripCost" : INT_TRIP_COST, String.valueOf(traveler.getTripCost().intValue()));
                }
            }
        }
    }

    private void addNames(MultiValueMap<String, String> params, List<GenericTraveler> travelers) {
        for (GenericTraveler traveler : travelers) {
            if (traveler.isPrimary()) {
                params.add(STR_FIRST_NAME, traveler.getFirstName());
                params.add(STR_LAST_NAME, traveler.getLastName());
            }
        }
        if (travelers.size() > 1) {
            for (GenericTraveler traveler : travelers) {
                if (!traveler.isPrimary()) {
                    params.add(STR_FIRST_NAME, traveler.getFirstName());
                    params.add(STR_LAST_NAME, traveler.getLastName());
                }
            }
        }
    }

    static Map<String, String> productFormNumbers = new HashMap<>();

    static {
        productFormNumbers.put(TravelexInsuranceClient.FLIGHT_INSURE_PLUS_PRODUCT_CODE, "FIPB-1117");
    }

    public String getProductFormNumber(String policyMetaCode) {
        return productFormNumbers.get(policyMetaCode);
    }

    static class TravelexUpsaleContext {
        boolean carRental;

        public TravelexUpsaleContext() {
        }

        public boolean isCarRental() {
            return carRental;
        }

        public void setCarRental(boolean carRental) {
            this.carRental = carRental;
        }
    }

}