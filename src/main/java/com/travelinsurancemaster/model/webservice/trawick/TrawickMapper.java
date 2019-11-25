package com.travelinsurancemaster.model.webservice.trawick;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.dto.PolicyMetaPackage;
import com.travelinsurancemaster.model.dto.PolicyMetaPackageValue;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.PolicyMetaCategoryValueService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.clients.TrawickClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Created by ritchie on 2/27/15.
 */
@Component
public class TrawickMapper {
    private static final String AGENT_ID = "agent_id";
    private static final String PRODUCT = "product";
    private static final String POLICY_MAX = "policy_max";
    private static final String MEDICAL_LIMIT = "medical_limit";
    private static final String EFF_DATE = "eff_date";
    private static final String TERM_DATE = "term_date";
    private static final String DESTINATION = "destination";
    private static final String COUNTRY = "country";
    private static final String DOB = "dob";
    private static final String SPORTS = "sports";
    private static final String HOME_COUNTRY = "home_country";
    private static final String DEDUCTIBLE = "deductible";
    private static final String PLAN = "plan";
    private static final String AD_D_UPGRADE = "ad-d_upgrade";
    private static final String TRIP_COST_PER_PERSON = "trip_cost_per_person";
    private static final String TRIP_PURCHASE_DATE = "trip_purchase_date";
    private static final String FLIGHT_ADD = "flight_add";
    private static final String CDW = "cdw";
    private static final String ENHANCED_MEDICAL_UPGRADE = "enhanced_med_upgrade";
    private static final String RENTAL_CAR = "car_rental_collision";
    private static final String PET_ASSIST = "petAssist";
    private static final String BAGGAGE_LOSS = "baggage";
    private static final String CANCEL_FOR_ANY_REASON = "cancelForAny";
    private static final String COMPLETE_ORDER = "completeOrder";
    private static final String TRAVELER_PREFIX = "t";
    private static final String FIRST = "First";
    private static final String MIDDLE = "Middle";
    private static final String LAST = "Last";
    private static final String GENDER = "Gender";
    private static final String MAIN_EMAIL = "mainEmail";
    private static final String PASSPORT = "passPort";
    private static final String BENEFICIARY = "beneficiary";
    private static final String RELATIONSHIP = "relationship";
    private static final String STREET = "street";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String ZIP = "zip";
    private static final String CITIZENSHIP = "citizenship";
    private static final String PHONE = "phone";
    private static final String CC_NAME = "cc_name";
    private static final String CC_STREET = "cc_street";
    private static final String CC_COUNTRY = "cc_country";
    private static final String CC_CITY = "cc_city";
    private static final String CC_STATECODE = "cc_statecode";
    private static final String CC_POSTALCODE = "cc_postalcode";
    private static final String CC_NUMBER = "cc_number";
    private static final String CC_MONTH = "cc_month";
    private static final String CC_YEAR = "cc_year";
    private static final String CC_CVV = "cc_cvv";
    private static final String POLICY_MAX_VALUE = "50000";
    private static final String DEDUCTIBLE_DEFAULT_VALUE = "0";
    private static final String COMPLETE_ORDER_VALUE = "true";
    private static final String GENDER_VALUE = "Male";
    private static final String HOMECOUNTRY = "homecountry";
    private static final String DATE_FORMAT = "MM/dd/yyyy";

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;
    @Autowired
    private PolicyMetaService policyMetaService;

    public MultiValueMap<String, String> toMap(QuoteRequest quoteRequest, PolicyMetaCode policyMetaCode, String user, PolicyMeta policyMeta) {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        requestParams.add(AGENT_ID, user);
        String policyCode = policyMetaCode.getCode();
        requestParams.add(PRODUCT, policyCode);
        addLimit(quoteRequest, policyCode, requestParams);
        requestParams.add(EFF_DATE, dateFormat.format(quoteRequest.getDepartDate()));
        requestParams.add(TERM_DATE, dateFormat.format(quoteRequest.getReturnDate()));
        requestParams.add(DESTINATION, quoteRequest.getDestinationCountry() != null ? quoteRequest.getDestinationCountry().name() : null);
        requestParams.add(COUNTRY, quoteRequest.getResidentCountry() != null ? quoteRequest.getResidentCountry().name() : null);
        for (int i = 0; i < quoteRequest.getTravelers().size(); i++) {
            GenericTraveler genericTraveler = quoteRequest.getTravelers().get(i);
            requestParams.add(DOB + (i + 1), dateFormat.format(genericTraveler.getBirthdaySafe()));
        }
        String deductible = quoteRequest.getUpsaleValue(CategoryCodes.MEDICAL_DEDUCTIBLE, DEDUCTIBLE_DEFAULT_VALUE);
        // for Safe Travels for Visitors to the USA plan deductible means plan!!!
        if (TrawickClient.SAFE_TRAVELS_FOR_VISITORS_TO_THE_USA.equals(policyCode)) {
            if ((StringUtils.isEmpty(deductible) || StringUtils.equals(deductible, "0")) && (TrawickClient.SAFE_TRAVELS_FOR_VISITORS_TO_THE_USA_DIAMOND_PLAN_UNIQUE_CODE.equals(policyMetaCode.getPolicyMeta().getUniqueCode())
                    || TrawickClient.SAFE_TRAVELS_FOR_VISITORS_TO_THE_USA_DIAMOND_PLUS_PLAN_UNIQUE_CODE.equals(policyMetaCode.getPolicyMeta().getUniqueCode()))) {
                // for diamond plan defauld value is 100 and plan code is 1376
                deductible = "100";
            }
            String deductibleAPIValue = policyMetaCategoryValueService.getApiValue(
                    policyMetaCode.getPolicyMeta().getId(), CategoryCodes.MEDICAL_DEDUCTIBLE, deductible, quoteRequest);
            quoteRequest.getUpsaleValues().put(CategoryCodes.MEDICAL_DEDUCTIBLE, deductible);
            // no other upsales are needed
            requestParams.add(PLAN, deductibleAPIValue);
            return requestParams;
        }
        requestParams.add(DEDUCTIBLE, deductible);


        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE)) {
            String flightOnlyAdAndD = quoteRequest.getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "0");
            String flightOnlyAdAndDApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMetaCode.getPolicyMeta().getId(), CategoryCodes.FLIGHT_ONLY_AD_AND_D, flightOnlyAdAndD, quoteRequest);
            requestParams.add(FLIGHT_ADD, flightOnlyAdAndDApiValue);

            String collisionDamageWaiver = quoteRequest.getUpsaleValue(CategoryCodes.RENTAL_CAR, "no");
            String collisionDamageWaiverApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMetaCode.getPolicyMeta().getId(), CategoryCodes.RENTAL_CAR, collisionDamageWaiver, quoteRequest);
            requestParams.add(CDW, collisionDamageWaiverApiValue);
        }

        // TODO: 09.11.2017 change default values
        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE)) {
            String petAssist = quoteRequest.getUpsaleValue(CategoryCodes.PET_ASSIST, "no");
            String petAssistApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMetaCode.getPolicyMeta().getId(), CategoryCodes.PET_ASSIST, petAssist, quoteRequest);
            requestParams.add(PET_ASSIST, petAssistApiValue);

            String baggageLoss = quoteRequest.getUpsaleValue(CategoryCodes.BAGGAGE_LOSS, "0");
            String baggageLossApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMetaCode.getPolicyMeta().getId(), CategoryCodes.BAGGAGE_LOSS, baggageLoss, quoteRequest);
            requestParams.add(BAGGAGE_LOSS, baggageLossApiValue);

            String cancelForAnyReason = quoteRequest.getUpsaleValue(CategoryCodes.CANCEL_FOR_ANY_REASON, "no");
            String cancelForAnyApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMetaCode.getPolicyMeta().getId(), CategoryCodes.CANCEL_FOR_ANY_REASON, cancelForAnyReason, quoteRequest);
            requestParams.add(CANCEL_FOR_ANY_REASON, cancelForAnyApiValue);
        }

        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_COST_SAVER_PLAN_CODE)
                || policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_COMPREHENSIVE_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_MULTINATIONAL_TRIP_CANCELLATION_PLAN_CODE)
                || policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_TRIP_CANCELLATION_PLAN_CODE)) {
            String homeCountryFollowMeHomeValue = quoteRequest.getUpsaleValue(CategoryCodes.HOME_COUNTRY_FOLLOW_ME_HOME);
            String homeCountryFollowMeHomeAPIValue = policyMetaCategoryValueService.getApiValue(
                    policyMetaCode.getPolicyMeta().getId(), CategoryCodes.HOME_COUNTRY_FOLLOW_ME_HOME, homeCountryFollowMeHomeValue, quoteRequest);
            requestParams.add(HOME_COUNTRY, homeCountryFollowMeHomeAPIValue);
        }
        if (!policyCode.equals(TrawickClient.SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE) &&
                !policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE) &&
                !policyCode.equals(TrawickClient.SAFE_TRAVELS_3_IN_1_PLAN_CODE) &&
                !policyCode.equals(TrawickClient.SAFE_TRAVELS_VACANTIONER_PLAN_CODE) &&
                !policyCode.equals(TrawickClient.SAFE_TRAVELS_SHENGEN_VISA) &&
                !policyCode.equals(TrawickClient.SAFE_TRAVELS_OUTBOUND) &&
                !policyCode.equals(TrawickClient.SAFE_TRAVELS_OUTBOUND_COST_SAVER)) {
            String twentyFourHourAdAndDValue = quoteRequest.getUpsaleValue(CategoryCodes.TWENTY_FOUR_HOUR_AD_AND_D, "25000");
            String twentyFourHourAdAndDApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMetaCode.getPolicyMeta().getId(), CategoryCodes.TWENTY_FOUR_HOUR_AD_AND_D, twentyFourHourAdAndDValue, quoteRequest);
            requestParams.add(AD_D_UPGRADE, twentyFourHourAdAndDApiValue);

            String sportsValue = quoteRequest.getUpsaleValue(CategoryCodes.AMATEUR_SPORTS, "no");
            String sportsApiValue = policyMetaCategoryValueService.getApiValue(policyMetaCode.getPolicyMeta().getId(), CategoryCodes.AMATEUR_SPORTS, sportsValue, quoteRequest);
            quoteRequest.getUpsaleValues().put(CategoryCodes.AMATEUR_SPORTS, sportsValue);
            requestParams.add(SPORTS, sportsApiValue);
        }
        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_VACANTIONER_PLAN_CODE)) {
            processEnhancedMedicalPackage(requestParams, policyMeta, quoteRequest);

            String rentalCar = quoteRequest.getUpsaleValue(CategoryCodes.RENTAL_CAR, "no");
            String rentalCarApiValue = policyMetaCategoryValueService.getApiValue(policyMetaCode.getPolicyMeta().getId(), CategoryCodes.RENTAL_CAR,
                    rentalCar, quoteRequest);
            requestParams.add(RENTAL_CAR, rentalCarApiValue);
        }
        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_TRIP_CANCELLATION_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_MULTINATIONAL_TRIP_CANCELLATION_PLAN_CODE)
                || policyCode.equals(TrawickClient.SAFE_TRAVELS_3_IN_1_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE)
                || policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_VACANTIONER_PLAN_CODE)) {


            if (quoteRequest.getPlanType().getId() == PlanType.LIMITED.getId())
                requestParams.add(TRIP_COST_PER_PERSON, "0");
            else {
                if (quoteRequest.getTravelers().get(0).getTripCost().compareTo(new BigDecimal(0E-10)) == 0)
                    requestParams.add(TRIP_COST_PER_PERSON, "0");
                else
                    requestParams.add(TRIP_COST_PER_PERSON, quoteRequest.getTravelers().get(0).getTripCost().toString());
            }


            requestParams.add(TRIP_PURCHASE_DATE, dateFormat.format(LocalDate.now()));

        }
        return requestParams;
    }

    private void addLimit(QuoteRequest request, String policyCode, MultiValueMap<String, String> params) {
        String policyMax = request.getUpsaleValue(CategoryCodes.EMERGENCY_MEDICAL, POLICY_MAX_VALUE);
        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_PLAN_CODE) ||
                policyCode.equals(TrawickClient.SAFE_TRAVELS_INTERNATIONAL_PLAN_CODE) ||
                policyCode.equals(TrawickClient.SAFE_TRAVELS_INTERNATIONAL_COST_SAVER_PLAN_CODE) ||
                policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_COST_SAVER_PLAN_CODE) ||
                policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_COMPREHENSIVE_PLAN_CODE) ||
                policyCode.equals(TrawickClient.SAFE_TRAVELS_OUTBOUND) ||
                policyCode.equals(TrawickClient.SAFE_TRAVELS_OUTBOUND_COST_SAVER)
                ) {
            params.add(POLICY_MAX, policyMax);
        } else if (policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_TRIP_CANCELLATION_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_MULTINATIONAL_TRIP_CANCELLATION_PLAN_CODE)) {
            params.add(MEDICAL_LIMIT, policyMax);
        }
    }

    public MultiValueMap<String, String> toMap(PurchaseRequest request, String user) {
        MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
        PolicyMeta policyMeta = request.getProduct().getPolicyMeta();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        purchaseParams.add(AGENT_ID, user);
        String policyCode = request.getProduct().getPolicyMetaCode().getCode();
        purchaseParams.add(PRODUCT, policyCode);
        QuoteRequest quoteRequest = request.getQuoteRequest();
        addLimit(quoteRequest, policyCode, purchaseParams);
        purchaseParams.add(EFF_DATE, dateFormat.format(request.getQuoteRequest().getDepartDate()));
        purchaseParams.add(TERM_DATE, dateFormat.format(request.getQuoteRequest().getReturnDate()));
        purchaseParams.add(DESTINATION, quoteRequest.getDestinationCountry() != null ? request.getQuoteRequest().getDestinationCountry().name() : null);
        purchaseParams.add(COUNTRY, quoteRequest.getResidentCountry() != null ? request.getQuoteRequest().getResidentCountry().name() : null);
        for (int i = 0; i < request.getTravelers().size(); i++) {
            GenericTraveler genericTraveler = request.getTravelers().get(i);
            if (i == 0) {
                purchaseParams.add(BENEFICIARY, genericTraveler.getBeneficiary());
                purchaseParams.add(RELATIONSHIP, genericTraveler.getBeneficiaryRelation().name());
            }
            purchaseParams.add(DOB + (i + 1), dateFormat.format(genericTraveler.getBirthdaySafe()));
            purchaseParams.add(TRAVELER_PREFIX + (i + 1) + FIRST, genericTraveler.getFirstName());
            purchaseParams.add(TRAVELER_PREFIX + (i + 1) + MIDDLE, genericTraveler.getMiddleInitials());
            purchaseParams.add(TRAVELER_PREFIX + (i + 1) + LAST, genericTraveler.getLastName());
            purchaseParams.add(TRAVELER_PREFIX + (i + 1) + GENDER, GENDER_VALUE); // todo: add gender?
        }
        // purchase params
        purchaseParams.add(COMPLETE_ORDER, COMPLETE_ORDER_VALUE);
        purchaseParams.add(MAIN_EMAIL, request.getEmail());
        purchaseParams.add(PASSPORT, "");
        purchaseParams.add(STREET, request.getAddress());
        purchaseParams.add(CITY, request.getCity());
        purchaseParams.add(STATE, request.getQuoteRequest().getResidentState() != null ? request.getQuoteRequest().getResidentState().name() : "");
        purchaseParams.add(ZIP, request.getPostalCode());
        purchaseParams.add(CITIZENSHIP, request.getQuoteRequest().getCitizenCountry().name());
        purchaseParams.add(PHONE, request.getPhone());
        purchaseParams.add(HOMECOUNTRY, request.getQuoteRequest().getResidentCountry().name());
        purchaseParams.add(CC_NAME, request.getCreditCard().getCcName());
        purchaseParams.add(CC_STREET, request.getCreditCard().getCcAddress());
        purchaseParams.add(CC_COUNTRY, request.getCreditCard().getCcCountry().name());
        purchaseParams.add(CC_CITY, request.getCreditCard().getCcCity());
        purchaseParams.add(CC_STATECODE, request.getCreditCard().getCcStateCode() != null ? request.getCreditCard().getCcStateCode().name() : "");
        purchaseParams.add(CC_POSTALCODE, request.getCreditCard().getCcZipCode());
        purchaseParams.add(CC_NUMBER, request.getCreditCard().getCcNumber().toString()); // Visa, Master Card or Discover Card
        purchaseParams.add(CC_MONTH, request.getCreditCard().getCcExpMonth());
        purchaseParams.add(CC_YEAR, request.getCreditCard().getCcExpYear());
        purchaseParams.add(CC_CVV, request.getCreditCard().getCcCode());
        // up-sales
        String deductible = request.getQuoteRequest().getUpsaleValue(CategoryCodes.MEDICAL_DEDUCTIBLE, DEDUCTIBLE_DEFAULT_VALUE);
        // for Safe Travels for Visitors to the USA plan deductible means plan!!!
        if (TrawickClient.SAFE_TRAVELS_FOR_VISITORS_TO_THE_USA.equals(policyCode)) {
            String deductibleAPIValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.MEDICAL_DEDUCTIBLE, deductible, quoteRequest);
            purchaseParams.add(PLAN, deductibleAPIValue);
            // no other upsales are needed
            return purchaseParams;
        }
        purchaseParams.add(DEDUCTIBLE, deductible);
        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE)) {
            String flightAdd = quoteRequest.getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "0");
            String flightAddApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.FLIGHT_ONLY_AD_AND_D, flightAdd, quoteRequest);
            purchaseParams.add(FLIGHT_ADD, flightAddApiValue);
        }
        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE)) {
            String cdw = quoteRequest.getUpsaleValue(CategoryCodes.RENTAL_CAR, "no");
            String cdwApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.RENTAL_CAR, cdw, quoteRequest);
            purchaseParams.add(CDW, cdwApiValue);
        }
        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE)) {
            String petAssist = quoteRequest.getUpsaleValue(CategoryCodes.PET_ASSIST, "no");
            String petAssistApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.PET_ASSIST, petAssist, quoteRequest);
            purchaseParams.add(PET_ASSIST, petAssistApiValue);
        }

        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE)) {
            String baggageOptionUpgrage = quoteRequest.getUpsaleValue(CategoryCodes.BAGGAGE_LOSS, "0");
            String baggageOptionUpgrageApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.BAGGAGE_LOSS, baggageOptionUpgrage, quoteRequest);
            purchaseParams.add(BAGGAGE_LOSS, baggageOptionUpgrageApiValue);
        }

        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE)) {
            String cancelForAny = quoteRequest.getUpsaleValue(CategoryCodes.CANCEL_FOR_ANY_REASON, "no");
            String cancelForAnyApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.CANCEL_FOR_ANY_REASON, cancelForAny, quoteRequest);
            purchaseParams.add(CANCEL_FOR_ANY_REASON, cancelForAnyApiValue);
        }

        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_COST_SAVER_PLAN_CODE)
                || policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_COMPREHENSIVE_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_MULTINATIONAL_TRIP_CANCELLATION_PLAN_CODE)
                || policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_TRIP_CANCELLATION_PLAN_CODE)) {
            String homeCountryFollowMeHomeValue = quoteRequest.getUpsaleValue(CategoryCodes.HOME_COUNTRY_FOLLOW_ME_HOME);
            String homeCountryFollowMeHomeAPIValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.HOME_COUNTRY_FOLLOW_ME_HOME, homeCountryFollowMeHomeValue, quoteRequest);
            purchaseParams.add(HOME_COUNTRY, homeCountryFollowMeHomeAPIValue);
        }
        if (!policyCode.equals(TrawickClient.SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE) && !policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE)
                && !policyCode.equals(TrawickClient.SAFE_TRAVELS_3_IN_1_PLAN_CODE) && policyCode.equals(TrawickClient.SAFE_TRAVELS_VACANTIONER_PLAN_CODE)
                && !policyCode.equals(TrawickClient.SAFE_TRAVELS_SHENGEN_VISA)) {
            String twentyFourHourAdAndDValue = request.getQuoteRequest().getUpsaleValue(CategoryCodes.TWENTY_FOUR_HOUR_AD_AND_D, "25000");
            String twentyFourHourAdAndDApiValue = policyMetaCategoryValueService.getApiValue(
                    policyMeta.getId(), CategoryCodes.TWENTY_FOUR_HOUR_AD_AND_D, twentyFourHourAdAndDValue, quoteRequest);
            purchaseParams.add(AD_D_UPGRADE, twentyFourHourAdAndDApiValue);

            String sportsValue = quoteRequest.getUpsaleValue(CategoryCodes.AMATEUR_SPORTS, "no");
            String sportsApiValue = policyMetaCategoryValueService.getApiValue(policyMeta.getId(), CategoryCodes.AMATEUR_SPORTS, sportsValue, quoteRequest);
            purchaseParams.add(SPORTS, sportsApiValue);
        }
        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_VACANTIONER_PLAN_CODE)) {
            String medicalUpgrade = quoteRequest.getUpsaleValue(CategoryCodes.NON_MEDICAL_EVACUATION, "no");
            String medicalUpgradeApiValue = policyMetaCategoryValueService.getApiValue(policyMeta.getId(),
                    CategoryCodes.NON_MEDICAL_EVACUATION, medicalUpgrade, quoteRequest);
            purchaseParams.add(ENHANCED_MEDICAL_UPGRADE, medicalUpgradeApiValue);

            processEnhancedMedicalPackage(purchaseParams, policyMeta, quoteRequest);

            String rentalCar = quoteRequest.getUpsaleValue(CategoryCodes.RENTAL_CAR, "no");
            String rentalCarApiValue = policyMetaCategoryValueService.getApiValue(policyMeta.getId(),
                    CategoryCodes.RENTAL_CAR, rentalCar, quoteRequest);
            purchaseParams.add(RENTAL_CAR, rentalCarApiValue);
        }
        if (policyCode.equals(TrawickClient.SAFE_TRAVELS_USA_TRIP_CANCELLATION_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_MULTINATIONAL_TRIP_CANCELLATION_PLAN_CODE)
                || policyCode.equals(TrawickClient.SAFE_TRAVELS_3_IN_1_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE)
                || policyCode.equals(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE) || policyCode.equals(TrawickClient.SAFE_TRAVELS_VACANTIONER_PLAN_CODE)) {

            purchaseParams.add(TRIP_COST_PER_PERSON, request.getTravelers().get(0).getTripCost().toString());

            purchaseParams.add(TRIP_PURCHASE_DATE, dateFormat.format(LocalDate.now()));
        }
        return purchaseParams;
    }

    private void processEnhancedMedicalPackage(MultiValueMap<String, String> purchaseParams, PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        List<PolicyMetaPackage> policyMetaPackages = policyMeta.getPolicyMetaPackages();
        if(policyMetaPackages.size() == 0){
            return;
        }

        PolicyMetaPackage policyMetaPackage = policyMetaPackages.get(0);

        boolean packageEnabled = false;
        for (PolicyMetaPackageValue policyMetaPackageValue : policyMetaPackage.getPolicyMetaPackageValues()) {
            if (Objects.equals(quoteRequest.getUpsaleValue(policyMetaPackageValue.getPolicyMetaCategory().getCategory().getCode(), "no"), policyMetaPackageValue.getValue())) {
                policyMetaService.enablePackage(policyMeta, quoteRequest, policyMetaPackage.getCode());
                purchaseParams.add(ENHANCED_MEDICAL_UPGRADE, "yes");
                packageEnabled = true;
                break;
            }
        }
        if (!packageEnabled) {
            policyMetaService.disablePackage(policyMeta, quoteRequest, policyMetaPackage.getCode());
            purchaseParams.add(ENHANCED_MEDICAL_UPGRADE, "no");
        }
    }
}
