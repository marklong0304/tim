package com.travelinsurancemaster.model.webservice.bhtravelprotection;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.PolicyMetaRepository;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.PolicyMetaCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maleev on 27.04.2016.
 */
@Component
public class BHTPMapper {

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private PolicyMetaRepository policyMetaRepository;

    public static DateTimeFormatter commonDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static Map<String, String> optionalCoverages = new HashMap<>();

    private static final String EXACT_CARE_EXTRA_PRODUCT_ID = "ECE";

    private final String AIR_CARE_UNIQUE_CODE = "AirCare";

    static {
        optionalCoverages.put(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "FAU");
        optionalCoverages.put(CategoryCodes.RENTAL_CAR, "CRCC");
        optionalCoverages.put(CategoryCodes.CANCEL_FOR_ANY_REASON, "CFAR");
        optionalCoverages.put(CategoryCodes.MEDICAL_EVACUATION, "EEU");
        optionalCoverages.put(CategoryCodes.EMERGENCY_MEDICAL, "MED");
        optionalCoverages.put(CategoryCodes.COMMON_CARRIER_AD_AND_D, "FAU");
        optionalCoverages.put(CategoryCodes.CANCEL_FOR_WORK_REASONS, "MED");
    }


    public AuthRequest createAuthRequest(InsuranceMasterApiProperties.BHTravelProtection bhtpProperties) {
        AuthRequest request = new AuthRequest();
        request.setUserName(bhtpProperties.getUser());
        request.setPassword(bhtpProperties.getPassword());

        return request;
    }

    public BHTPQuoteRequest createQuoteRequest(QuoteRequest quoteRequest, PolicyMeta policyMeta, String policyMetaCode, InsuranceMasterApiProperties.BHTravelProtection bhtpProperties) {
        BHTPQuoteRequest bhtpQuoteRequest = new BHTPQuoteRequest();


        bhtpQuoteRequest.setPolicy(createBhtpPolicy(quoteRequest, policyMetaCode, bhtpProperties));
        bhtpQuoteRequest.setTravelers(createTravelers(quoteRequest));

        proccessUpsales(quoteRequest, policyMeta, bhtpQuoteRequest);

        return bhtpQuoteRequest;
    }

    private void proccessUpsales(QuoteRequest quoteRequest, PolicyMeta policyMeta, BHTPQuoteRequest bhtpQuoteRequest) {
        if (quoteRequest.getUpsaleValues().size() == 0) return;

        for (String categoryCode : quoteRequest.getUpsaleValues().keySet()) {
            String upsaleValue = quoteRequest.getUpsaleValues().get(categoryCode);

            if (policyMetaCategoryService.getCategoryValues(policyMeta.getId(), categoryCode, quoteRequest).get(0).getValue().equals(upsaleValue)) {
                continue;
            }

            String apiName = optionalCoverages.get(categoryCode);
            if (apiName == null) continue;

            BHTPCoverage coverage = new BHTPCoverage();
            coverage.setRatingId(apiName);

            switch (categoryCode) {
                case CategoryCodes.COMMON_CARRIER_AD_AND_D:
                    coverage.setCoverageLimit(Integer.parseInt(upsaleValue));
                case CategoryCodes.FLIGHT_ONLY_AD_AND_D:
                    coverage.setCoverageLimit(Integer.parseInt(upsaleValue));
                    break;
            }

            bhtpQuoteRequest.getCoverages().add(coverage);
        }
    }

    public BHTPQuoteRequest createPurchaseRequest(PurchaseRequest purchaseRequest, InsuranceMasterApiProperties.BHTravelProtection bhtpProperties) {

        BHTPQuoteRequest bhtpPurchaseRequest = new BHTPQuoteRequest();
        bhtpPurchaseRequest.setPolicy(createBhtpPolicy(purchaseRequest.getQuoteRequest(), purchaseRequest.getProduct().getPolicyMetaCode().getCode(), bhtpProperties));
        bhtpPurchaseRequest.getPolicy().setFulfillmentMethod("Email");
        bhtpPurchaseRequest.getPolicy().setIsQuickQuote(false);
        bhtpPurchaseRequest.setTravelers(createTravelers(purchaseRequest));
        bhtpPurchaseRequest.setPaymentInformation(createPaymentInformation(purchaseRequest.getCreditCard()));

        if (purchaseRequest.getProduct().getPolicyMeta().getUniqueCode().equals(AIR_CARE_UNIQUE_CODE)) {
            purchaseRequest.getFlights().forEach(flight -> {
                BHTPFlight bhtpFlight = new BHTPFlight();
                bhtpFlight.setDepartureAirportCode(flight.getDepartureAirportCode());
                bhtpFlight.setAirlineCode(flight.getAirlineCode());
                bhtpFlight.setArrivalAirportCode(flight.getArrivalAirportCode());
                bhtpFlight.setFlightNumber(flight.getFlightNumber());
                bhtpFlight.setDepartureDate(bhtpPurchaseRequest.getPolicy().getDepartureDate());
                bhtpPurchaseRequest.getFlights().add(bhtpFlight);
            });
        }

        proccessUpsales(purchaseRequest.getQuoteRequest(), purchaseRequest.getProduct().getPolicyMeta(), bhtpPurchaseRequest);

        return bhtpPurchaseRequest;
    }

    private BHTPPolicy createBhtpPolicy(QuoteRequest quoteRequest, String policyMetaCode, InsuranceMasterApiProperties.BHTravelProtection bhtpProperties) {
        BHTPPolicy bhtpPolicy = new BHTPPolicy();
        bhtpPolicy.setAgencyCode(bhtpProperties.getAgencyCode());
        bhtpPolicy.setAgentCode(bhtpProperties.getAgentCode());

        bhtpPolicy.setDepartureDate(commonDateFormat.format(quoteRequest.getDepartDate()));
        bhtpPolicy.setReturnDate(commonDateFormat.format(quoteRequest.getReturnDate()));
        bhtpPolicy.setRentalCarPickupDate(commonDateFormat.format(quoteRequest.getRentalCarStartDateSafe()));
        bhtpPolicy.setRentalCarReturnDate(commonDateFormat.format(quoteRequest.getRentalCarEndDateSafe()));
        if (!policyMetaCode.equals(AIR_CARE_UNIQUE_CODE)) {
            if (quoteRequest.getDepositDate() != null) {
                bhtpPolicy.setTripDepositDate(commonDateFormat.format(quoteRequest.getDepositDate()));
            } else {
                bhtpPolicy.setTripDepositDate(commonDateFormat.format(LocalDate.now()));
            }
        }
        bhtpPolicy.setDestinationCountry(quoteRequest.getDestinationCountry().name());
        bhtpPolicy.setProductId(policyMetaCode);

        if (EXACT_CARE_EXTRA_PRODUCT_ID.equals(bhtpPolicy.getProductId())) {
            bhtpPolicy.setOverrideRequiresFlights(true);
            bhtpPolicy.setFutureFlightsExpected(true);
        }

        return bhtpPolicy;
    }

    private List<BHTPTraveler> createTravelers(QuoteRequest quoteRequest) {
        List<BHTPTraveler> travelers = new ArrayList<>(quoteRequest.getTravelers().size());
        quoteRequest.getTravelers().forEach(traveler -> travelers.add(createTraveler(traveler, quoteRequest)));
        return travelers;
    }

    private BHTPTraveler createTraveler(GenericTraveler traveler, QuoteRequest quoteRequest) {
        BHTPAddress bhtpAddress = new BHTPAddress();
        bhtpAddress.setCountry(quoteRequest.getResidentCountry().name());
        bhtpAddress.setStateOrProvince(quoteRequest.getResidentState().name());

        BHTPTraveler bhtpTraveler = new BHTPTraveler();
        bhtpTraveler.setAge(traveler.getAge());
        bhtpTraveler.setAddress(bhtpAddress);

        if (quoteRequest.getPlanType().getId() == PlanType.LIMITED.getId()) {
            bhtpTraveler.setTripCost(BigDecimal.valueOf(0).intValue());
        } else {
            bhtpTraveler.setTripCost(traveler.getTripCost().intValue());
        }

        bhtpTraveler.setIsPrimary(traveler.isPrimary());

        return bhtpTraveler;
    }

    private List<BHTPTraveler> createTravelers(PurchaseRequest purchaseRequest) {
        List<BHTPTraveler> travelers = new ArrayList<>(purchaseRequest.getTravelers().size());

        purchaseRequest.getTravelers().forEach(traveler -> {
            BHTPTraveler bhtpTraveler = createTraveler(traveler, purchaseRequest.getQuoteRequest());

            bhtpTraveler.setFirstName(traveler.getFirstName());
            bhtpTraveler.setLastName(traveler.getLastName());
            bhtpTraveler.setBirthDate(commonDateFormat.format(traveler.getBirthday()));

            if (traveler.isPrimary()) {
                bhtpTraveler.setEmail(purchaseRequest.getEmail());
                bhtpTraveler.setPhoneNumber(purchaseRequest.getPhone());

                bhtpTraveler.getAddress().setCity(purchaseRequest.getCity());
                bhtpTraveler.getAddress().setAddress1(purchaseRequest.getAddress());
                bhtpTraveler.getAddress().setPostalCode(purchaseRequest.getPostalCode());

            }

            travelers.add(bhtpTraveler);
        });

        return travelers;
    }

    private BHTPPaymentInformation createPaymentInformation(CreditCard creditCard) {
        BHTPPaymentInformation bhtpPaymentInformation = new BHTPPaymentInformation();

        bhtpPaymentInformation.setNameOnCard(creditCard.getCcName());
        bhtpPaymentInformation.setAddress(creditCard.getCcAddress());
        bhtpPaymentInformation.setCardNumber(creditCard.getCcNumber().toString());
        bhtpPaymentInformation.setCardCode(creditCard.getCcCode());
        bhtpPaymentInformation.setExpirationDate(creditCard.getCcExpMonth() + "/" + creditCard.getCcExpYear().substring(2));
        bhtpPaymentInformation.setCity(creditCard.getCcCity());
        bhtpPaymentInformation.setState(creditCard.getCcStateCode().name());
        bhtpPaymentInformation.setZip(creditCard.getCcZipCode());

        return bhtpPaymentInformation;
    }
}
