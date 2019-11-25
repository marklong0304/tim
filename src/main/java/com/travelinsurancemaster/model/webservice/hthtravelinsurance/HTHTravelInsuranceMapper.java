package com.travelinsurancemaster.model.webservice.hthtravelinsurance;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.webservice.common.CardType;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.PolicyMetaCategoryValueService;
import com.travelinsurancemaster.services.clients.HTHTravelInsuranceClient;
import com.travelinsurancemaster.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;

/**
 * Created by ritchie on 6/16/15.
 */
@Component
public class HTHTravelInsuranceMapper {

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    public static final String P_PRD_TYPE_ID = "p_prd_type_id";
    public static final String P_DEDUCTIBLE_LEVEL = "p_deductible_level";
    public static final String P_MAXIMUM_BENEFIT_LEVEL = "p_maximum_benefit_level";
    public static final String P_FULFILLMENT_TYPE = "p_fulfillment_type";
    public static final String P_INSURED_DEPENDENTS = "p_insured_dependents";
    public static final String P_AMOUNT = "p_amount";
    public static final String P_CARDTYPE = "p_cardtype";
    public static final String P_NAME_ON_CREDIT_CARD = "p_name_on_credit_card";
    public static final String P_CARDNUM = "p_cardnum";
    public static final String P_EXP_MONTH = "p_exp_month";
    public static final String P_EXP_YEAR = "p_exp_year";
    public static final String P_LINK_ID = "p_link_id";
    public static final String P_DATE = "p_date";
    public static final String P_RETURN_DATE = "p_return_date";
    public static final String P_FIRSTNAME = "p_firstname";
    public static final String P_MIDNAME = "p_midname";
    public static final String P_LASTNAME = "p_lastname";
    public static final String P_DOB = "p_dob";
    public static final String P_ADDRESS1 = "p_address1";
    public static final String P_ADDRESS2 = "p_address2";
    public static final String P_CITY = "p_city";
    public static final String P_STATE = "p_state";
    public static final String P_ZIP = "p_zip";
    public static final String P_COUNTRY = "p_country";
    public static final String P_PHONE = "p_phone";
    public static final String P_EMAIL = "p_email";
    public static final String PRD_TYPE_ID = "prd_type_id";
    public static final String AGENT_ID = "agent_id";
    public static final String TRAVEL_DESTINATION = "travel_destination";
    public static final String INITIAL_DEPOSIT_DATE = "initial_deposit_date";
    public static final String FINAL_PAYMENT_DATE = "final_payment_date";
    public static final String TRIP_DEPARTURE_DATE = "trip_departure_date";
    public static final String TRIP_RETURN_DATE = "trip_return_date";
    public static final String AFF_CALC_TOTAL_COST = "aff_calc_total_cost";
    public static final String PRIMARY_AIRLINE = "primary_airline";
    public static final String CRUISE_TOUR_OPERATOR = "cruise_tour_operator";
    public static final String AGENT_EMAIL = "agent_email";
    public static final String MED_LIMIT = "med_limit";
    public static final String DEDUCTIBLE = "deductible";
    public static final String FULFILLMENT_TYPE = "fulfillment_type";
    public static final String PRIM_MEM_FIRSTNAME = "prim_mem_firstname";
    public static final String PRIM_MEM_MIDNAME = "prim_mem_midname";
    public static final String PRIM_MEM_LASTNAME = "prim_mem_lastname";
    public static final String PRIM_MEM_DOB = "prim_mem_dob";
    public static final String PRIM_MEM_TRIP_COST = "prim_mem_trip_cost";
    public static final String PRIM_MEM_ADDRESS1 = "prim_mem_address1";
    public static final String PRIM_MEM_ADDRESS2 = "prim_mem_address2";
    public static final String PRIM_MEM_CITY = "prim_mem_city";
    public static final String PRIM_MEM_STATE = "prim_mem_state";
    public static final String PRIM_MEM_ZIP = "prim_mem_zip";
    public static final String PRIM_MEM_COUNTRY = "prim_mem_country";
    public static final String PRIM_MEM_TEL = "prim_mem_tel";
    public static final String PRIM_MEM_WORK_PHONE = "prim_mem_work_phone";
    public static final String PRIM_MEM_EMAIL = "prim_mem_email";
    public static final String BENEFICIARY_FIRSTNAME = "beneficiary_firstname";
    public static final String BENEFICIARY_LASTNAME = "beneficiary_lastname";
    public static final String BENEFICIARY_RELATION = "beneficiary_relation";
    public static final String CREDIT_CARD_NUMBER = "credit_card_number";
    public static final String CREDIT_CARD_TYPE = "credit_card_type";
    public static final String NAME_ON_CREDIT_CARD = "name_on_credit_card";
    public static final String CREDIT_CARD_MONTH = "credit_card_month";
    public static final String CREDIT_CARD_YEAR = "credit_card_year";
    public static final String P_GENDER = "p_gender";
    public static final String P_BENEFICIARY_FIRSTNAME = "p_beneficiary_firstname";
    public static final String P_BENEFICIARY_LASTNAME = "p_beneficiary_lastname";
    private static final String P_BENEFICIARY_RELATION = "p_beneficiary_relation";
    public static final String CFAR_FLAG = "CFAR_flag";

    public static final String DEFAULT_DEDUCTIBLE_LEVEL = "0";
    public static final String DEFAULT_MAXIMUM_BENEFIT_LEVEL = "50000";
    public static final String EMAIL_FULFILLMENT_TYPE = "EMAIL";
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String USA = "USA";
    private static final String FIRSTNAME = "firstname_d";
    private static final String MIDNAME = "midname_d";
    private static final String LASTNAME = "lastname_d";
    private static final String DOB = "dob_d";
    private static final String TRIP_COST = "trip_cost_d";

    public MultiValueMap<String, String> toSingleAndMultiTripMap(PurchaseRequest request, InsuranceMasterApiProperties.HTHTravelInsurance hthTravelInsuranceProperties) {
        MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        String policyCode = request.getProduct().getPolicyMetaCode().getCode();
        requestParameters.add(P_PRD_TYPE_ID, policyCode);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        if (policyCode.equals(HTHTravelInsuranceClient.EXCURSION_PRODUCT_CODE) ||
                policyCode.equals(HTHTravelInsuranceClient.VOYAGER_PRODUCT_CODE)) {
            String deductibleLevel = request.getQuoteRequest().getUpsaleValue(CategoryCodes.MEDICAL_DEDUCTIBLE, DEFAULT_DEDUCTIBLE_LEVEL);
            requestParameters.add(P_DEDUCTIBLE_LEVEL, deductibleLevel);
            String maximumBenefitLevel = request.getQuoteRequest().getUpsaleValue(CategoryCodes.EMERGENCY_MEDICAL, DEFAULT_MAXIMUM_BENEFIT_LEVEL);
            requestParameters.add(P_MAXIMUM_BENEFIT_LEVEL, maximumBenefitLevel);
            requestParameters.add(P_RETURN_DATE, dateFormat.format(request.getQuoteRequest().getReturnDate()));
        }
        requestParameters.add(P_FULFILLMENT_TYPE, EMAIL_FULFILLMENT_TYPE);
        requestParameters.add(P_INSURED_DEPENDENTS, String.valueOf(request.getTravelers().size()));
        requestParameters.add(P_AMOUNT, String.valueOf(request.getProduct().getTotalPrice()));
        String cardType = getCardType(request.getCreditCard().getCcType());
        requestParameters.add(P_CARDTYPE, cardType);
        requestParameters.add(P_NAME_ON_CREDIT_CARD, request.getCreditCard().getCcName());
        requestParameters.add(P_CARDNUM, String.valueOf(request.getCreditCard().getCcNumber()));
        requestParameters.add(P_EXP_MONTH, request.getCreditCard().getCcExpMonth());
        requestParameters.add(P_EXP_YEAR, request.getCreditCard().getCcExpYear());
        requestParameters.add(P_LINK_ID, hthTravelInsuranceProperties.getUniqueId());
        requestParameters.add(P_DATE, dateFormat.format(request.getQuoteRequest().getDepartDate()));
        GenericTraveler primaryTraveler = null;
        for (GenericTraveler traveler : request.getTravelers()) {
            if (traveler.isPrimary()) {
                primaryTraveler = traveler;
                break;
            }
        }
        requestParameters.add(P_FIRSTNAME, primaryTraveler.getFirstName());
        requestParameters.add(P_MIDNAME, primaryTraveler.getMiddleInitials());
        requestParameters.add(P_LASTNAME, primaryTraveler.getLastName());
        requestParameters.add(P_DOB, dateFormat.format(primaryTraveler.getBirthdaySafe()));
        GenericTraveler genericTraveler = request.getTravelers().get(0);
        String[] beneficiaryNames = TextUtils.getNamesFromFullName(genericTraveler.getBeneficiary());
        requestParameters.add(P_BENEFICIARY_FIRSTNAME, beneficiaryNames[0]);
        requestParameters.add(P_BENEFICIARY_LASTNAME, beneficiaryNames[1]);
        requestParameters.add(P_BENEFICIARY_RELATION, genericTraveler.getBeneficiaryRelation().name());
        int i = 1;
        for (GenericTraveler traveler : request.getTravelers()) {
            if (!traveler.isPrimary()) {
                requestParameters.add(P_FIRSTNAME + "_d" + i, traveler.getFirstName());
                requestParameters.add(P_MIDNAME + "_d" + i, traveler.getMiddleInitials());
                requestParameters.add(P_LASTNAME + "_d" + i, traveler.getLastName());
                requestParameters.add(P_DOB + "_d" + i, dateFormat.format(traveler.getBirthdaySafe()));
                i++;
            }
        }
        requestParameters.add(P_ADDRESS1, request.getAddress());
        requestParameters.add(P_ADDRESS2, "");
        requestParameters.add(P_CITY, request.getCity());
        requestParameters.add(P_STATE, request.getQuoteRequest().getResidentState().name());
        requestParameters.add(P_ZIP, request.getPostalCode());
        requestParameters.add(P_COUNTRY, USA);
        requestParameters.add(P_PHONE, request.getPhone());
        requestParameters.add(P_EMAIL, request.getEmail());
        return requestParameters;
    }

    private static String getCardType(CardType ccType) {
        String cardType;
        switch (ccType) {
            case VISA:
                cardType = "Visa";
                break;
            case MasterCard:
                cardType = "MasterCard";
                break;
            case Discover:
                cardType = "Discover";
                break;
            case AmericanExpress:
                cardType = "American Express";
                break;
            default:
                cardType = "";
        }
        return cardType;
    }

    public MultiValueMap<String, String> toTripProtectorMap(PurchaseRequest request, InsuranceMasterApiProperties.HTHTravelInsurance hthTravelInsuranceProperties) {
        MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add(PRD_TYPE_ID, request.getProduct().getPolicyMetaCode().getCode());
        requestParameters.add(AGENT_ID, hthTravelInsuranceProperties.getUniqueId());
        requestParameters.add(TRAVEL_DESTINATION, request.getQuoteRequest().getDestinationCountry().getCaption());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        requestParameters.add(INITIAL_DEPOSIT_DATE, request.getQuoteRequest().getDepositDate() != null ? dateFormat.format(request.getQuoteRequest().getDepositDate()) : "");
        requestParameters.add(FINAL_PAYMENT_DATE, request.getQuoteRequest().getPaymentDate() != null ? dateFormat.format(request.getQuoteRequest().getPaymentDate()) : "");
        requestParameters.add(TRIP_DEPARTURE_DATE, dateFormat.format(request.getQuoteRequest().getDepartDate()));
        requestParameters.add(TRIP_RETURN_DATE, dateFormat.format(request.getQuoteRequest().getReturnDate()));
        requestParameters.add(AFF_CALC_TOTAL_COST, String.valueOf(request.getProduct().getTotalPrice()));
        requestParameters.add(PRIMARY_AIRLINE, "");
        requestParameters.add(CRUISE_TOUR_OPERATOR, "");
        requestParameters.add(AGENT_EMAIL, hthTravelInsuranceProperties.getAgentEmail());
        requestParameters.add(MED_LIMIT, "");
        requestParameters.add(DEDUCTIBLE, "");
        requestParameters.add(FULFILLMENT_TYPE, EMAIL_FULFILLMENT_TYPE);
        GenericTraveler primaryTraveler = null;
        for (GenericTraveler traveler : request.getTravelers()) {
            if (traveler.isPrimary()) {
                primaryTraveler = traveler;
                break;
            }
        }
        requestParameters.add(PRIM_MEM_FIRSTNAME, primaryTraveler.getFirstName());
        requestParameters.add(PRIM_MEM_MIDNAME, primaryTraveler.getMiddleInitials());
        requestParameters.add(PRIM_MEM_LASTNAME, primaryTraveler.getLastName());
        requestParameters.add(PRIM_MEM_DOB, dateFormat.format(primaryTraveler.getBirthdaySafe()));
        requestParameters.add(PRIM_MEM_TRIP_COST, String.valueOf(primaryTraveler.getTripCost()));
        requestParameters.add(PRIM_MEM_ADDRESS1, request.getAddress());
        requestParameters.add(PRIM_MEM_ADDRESS2, "");
        requestParameters.add(PRIM_MEM_CITY, request.getCity());
        requestParameters.add(PRIM_MEM_STATE, request.getQuoteRequest().getResidentState().name());
        requestParameters.add(PRIM_MEM_ZIP, request.getPostalCode());
        requestParameters.add(PRIM_MEM_COUNTRY, USA);
        requestParameters.add(PRIM_MEM_TEL, request.getPhone());
        requestParameters.add(PRIM_MEM_WORK_PHONE, "");
        requestParameters.add(PRIM_MEM_EMAIL, request.getEmail());
        GenericTraveler genericTraveler = request.getTravelers().get(0);
        String[] beneficiaryNames = TextUtils.getNamesFromFullName(genericTraveler.getBeneficiary());
        requestParameters.add(BENEFICIARY_FIRSTNAME, beneficiaryNames[0]);
        requestParameters.add(BENEFICIARY_LASTNAME, beneficiaryNames[1]);
        requestParameters.add(BENEFICIARY_RELATION, genericTraveler.getBeneficiaryRelation().name());
        requestParameters.add(CREDIT_CARD_NUMBER, String.valueOf(request.getCreditCard().getCcNumber()));
        String cardType = getCardType(request.getCreditCard().getCcType());
        requestParameters.add(CREDIT_CARD_TYPE, cardType);
        requestParameters.add(NAME_ON_CREDIT_CARD, request.getCreditCard().getCcName());
        requestParameters.add(CREDIT_CARD_MONTH, request.getCreditCard().getCcExpMonth());
        requestParameters.add(CREDIT_CARD_YEAR, request.getCreditCard().getCcExpYear());
        int i = 1;
        for (GenericTraveler traveler : request.getTravelers()) {
            if (!traveler.isPrimary()) {
                requestParameters.add(FIRSTNAME + i, traveler.getFirstName());
                requestParameters.add(MIDNAME + i, traveler.getMiddleInitials());
                requestParameters.add(LASTNAME + i, traveler.getLastName());
                requestParameters.add(DOB + i, dateFormat.format(traveler.getBirthdaySafe()));
                requestParameters.add(TRIP_COST + i, String.valueOf(traveler.getTripCost()));
                i++;
            }
        }
        String cancelForAnyReasonValue = request.getQuoteRequest().getUpsaleValue(CategoryCodes.CANCEL_FOR_ANY_REASON, "");
        if (!cancelForAnyReasonValue.isEmpty()) {
            String cancelForAnyReasonApiValue = policyMetaCategoryValueService.getApiValue(
                    request.getProduct().getPolicyMeta().getId(), CategoryCodes.CANCEL_FOR_ANY_REASON, cancelForAnyReasonValue, request.getQuoteRequest());
            requestParameters.add(CFAR_FLAG, cancelForAnyReasonApiValue);
        }
        return requestParameters;
    }
}
