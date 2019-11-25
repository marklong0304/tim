package com.travelinsurancemaster.model.webservice.travelinsure;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class TIMapper {

    public static DateTimeFormatter commonDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private TiPlanTables planTables = new TiPlanTables();

    public TIQuoteRequest createPurchaseRequest(PurchaseRequest request, InsuranceMasterApiProperties.TravelInsure travelInsure) {

        TIQuoteRequest tiQuoteRequest = new TIQuoteRequest();

        tiQuoteRequest.setVersion("1.0");
        tiQuoteRequest.setProducerCode("70002");

        PolicyMetaCode policyMetaCode = request.getProduct().getPolicyMetaCode();

        tiQuoteRequest.setRaterRequest(createRaterRequest(request.getQuoteRequest(), null, policyMetaCode, travelInsure));
        tiQuoteRequest.setSettlementRequest(createSettlementRequest(request.getQuoteRequest(), policyMetaCode, travelInsure));

        GenericTraveler traveler = getPrimary(request.getTravelers());

        TIRatePerson person = getPrimary2(tiQuoteRequest.getRaterRequest().getRatePersons());
        person.setFirstName(traveler.getFirstName());
        person.setLastName(traveler.getLastName());
        person.setAddress(request.getAddress());
        person.setCity(request.getCity());
        person.setState(request.getCreditCard().getCcStateCode().name());
        person.setPostal(request.getPostalCode());
        person.setCountry(request.getCreditCard().getCcCountry().name());
        person.setPhone(request.getPhone());
        person.setEmail(request.getEmail());
        person.setDateOfBirth(commonDateFormat.format(traveler.getBirthday()));

        TISettlementRequest tiSettlementRequest = tiQuoteRequest.getSettlementRequest();
        tiSettlementRequest.setPayerFirstName(traveler.getFirstName());
        tiSettlementRequest.setPayerLastName(traveler.getLastName());
        tiSettlementRequest.setAccountNumber(request.getCreditCard().getCcNumber().toString());
        tiSettlementRequest.setExpirationDate(request.getCreditCard().getCcExpMonth() + "/" + request.getCreditCard().getCcExpYear().substring(2));
        tiSettlementRequest.setBillingState(request.getCreditCard().getCcStateCode().name());
        tiSettlementRequest.setSecurityCode(request.getCreditCard().getCcCode());
        tiSettlementRequest.setBillingCountry(request.getCreditCard().getCcCountry().name());
        tiSettlementRequest.setBillingZip(request.getPostalCode());
        tiSettlementRequest.setBillingPhone(request.getPhone());
        tiSettlementRequest.setBillingCity(request.getCity());
        tiSettlementRequest.setBillingAddress(request.getAddress());
        tiSettlementRequest.setBillingEMail(request.getEmail());
        return tiQuoteRequest;
    }

    private TIRatePerson getPrimary2(List<TIRatePerson> ratePersons) {
        for (TIRatePerson person : ratePersons) {
            if(person.getPersonType().equals("Primary")) {
                return person;
            }
        }
        return ratePersons.get(0);
    }

    private GenericTraveler getPrimary(List<GenericTraveler> travelers) {
        for (GenericTraveler traveler : travelers) {
            if(traveler.isPrimary()) {
                return traveler;
            }
        }
        return travelers.get(0);
    }

    public TIQuoteRequest createQuoteRequest(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode,
                                             InsuranceMasterApiProperties.TravelInsure travelInsure) {

        TIQuoteRequest tiQuoteRequest = new TIQuoteRequest();

        tiQuoteRequest.setVersion("1.0");
        tiQuoteRequest.setProducerCode("70002");

        tiQuoteRequest.setRaterRequest(createRaterRequest(request, policyMeta, policyMetaCode, travelInsure));
        tiQuoteRequest.setSettlementRequest(createSettlementRequest(request, policyMetaCode, travelInsure));

        return tiQuoteRequest;
    }

    private TIRaterRequest createRaterRequest(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode,
                                              InsuranceMasterApiProperties.TravelInsure travelInsure) {

        TIRaterRequest tiRaterRequest = new TIRaterRequest();

        TIRaterPlan tiRaterPlan = new TIRaterPlan();

        tiRaterPlan.setPlanName(policyMetaCode.getName());

        tiRaterPlan.setTier("B");

        boolean typeC = false;

        String cancelAnyReasonUpsale = request.getUpsaleValue(CategoryCodes.CANCEL_FOR_ANY_REASON, "");
        if (!cancelAnyReasonUpsale.isEmpty()) {
            tiRaterPlan.setTier("C");
            typeC = true;
        }


        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tripStartDate = df1.format(request.getDepartDate());
        tiRaterPlan.setTripStartDate(tripStartDate);

        String tripEndDate = df1.format(request.getReturnDate());
        tiRaterPlan.setTripEndDate(tripEndDate);

        BigDecimal premium = estimatePremium(request, policyMeta, policyMetaCode, typeC);

        tiRaterPlan.setPremium(premium.doubleValue());

        tiRaterPlan.setPaymentFrequency("Single");
        tiRaterPlan.getExtraAttributes().add(
            new TIExtraAttribute("DestinationCountryCodes", request.getDestinationCountry().name())
        );
        tiRaterRequest.setRaterPlan(tiRaterPlan);

        if(request.getDepositDate() != null) {
            String initialDepositDate = df1.format(request.getDepositDate());
            tiRaterRequest.setInitialDepositDate(initialDepositDate);
        }

        if(request.getPaymentDate() != null) {
            String finalPaymentDate = df1.format(request.getPaymentDate());
            tiRaterRequest.setFinalPaymentDate(finalPaymentDate);
        }

        for(GenericTraveler traveler : request.getTravelers()) {

            TIRatePerson tiRatePerson = new TIRatePerson();

            if(traveler.isPrimary())
                tiRatePerson.setPersonType("Primary"); //“Primary”, “Spouse”, “Dependent”, “Other”
            else
                tiRatePerson.setPersonType("Other");

            tiRatePerson.setFirstName("Ima");
            tiRatePerson.setLastName("Test");
            tiRatePerson.setAddress("123 Sesame Street");
            tiRatePerson.setCity("Grand Rapids");
            tiRatePerson.setState("MA");
            tiRatePerson.setPostal("49508");
            tiRatePerson.setCountry("US");
            tiRatePerson.setGender("F"); // “F” or “M”


            LocalDateTime ldt = LocalDateTime.now();
            ldt = ldt.minusYears(traveler.getAge()).withMonth(Month.JANUARY.getValue()).withDayOfMonth(1);
            String dateOfBirth = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(ldt);

            tiRatePerson.setDateOfBirth(dateOfBirth);

            tiRatePerson.setPhone("616-555-1212");
            tiRatePerson.setSecondaryPhone("616-555-2323");
            tiRatePerson.setEmail("ima.test@usi.com");
            tiRatePerson.setEmergencyContactName("");
            tiRatePerson.setEmergencyContactPhone("");
            tiRatePerson.getExtraAttributes().add(new TIExtraAttribute("Organization", "Acme, Inc"));
            tiRatePerson.getRateFactors().add(new TIRateFactor("TripCost", request.getTripCost()));

            tiRaterRequest.getRatePersons().add(tiRatePerson);

        }

        return tiRaterRequest;
    }

    public BigDecimal estimatePremium(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode, boolean typeC) {

        BigDecimal premium = BigDecimal.ZERO;

        TiPlanType planType = TiPlanType.getByName(policyMetaCode.getName());
        List<GenericTraveler> travelers = request.getTravelers();
        Integer tripCost = 0;

        if(request.getPlanType().getId() == PlanType.LIMITED.getId()) {

            if(policyMeta != null && policyMeta.isSupportsZeroCancellation())
                tripCost = 0;
            else if(policyMeta != null && policyMeta.hasMinimalTripCost())
                tripCost = policyMeta.getMinimalTripCost().intValue();
            else
                tripCost = 1;

        }
        else {
            tripCost = request.getTripCost().intValue();
        }

        for(GenericTraveler traveler : travelers) {
            Integer age = traveler.getAge();
            BigDecimal travelerPlanPrice = planTables.find(age, tripCost, planType);
            premium = premium.add(travelerPlanPrice);
        }

        if(typeC) {
            TiCfarLoadFactor loadFactor = new TiCfarLoadFactor();
            premium = loadFactor.estimate(planType, premium);
        }

        return premium;
    }

    private TISettlementRequest createSettlementRequest(QuoteRequest request, PolicyMetaCode policyMetaCode,
                                                        InsuranceMasterApiProperties.TravelInsure travelInsure) {
        TISettlementRequest tiSettlementRequest = new TISettlementRequest();

        tiSettlementRequest.setPayerFirstName("Ima");
        tiSettlementRequest.setPayerLastName("Test");
        tiSettlementRequest.setAccountNumber("4444333322221111");
        tiSettlementRequest.setExpirationDate("11/2020");
        tiSettlementRequest.setSecurityCode("999");
        tiSettlementRequest.setBillingAddress("123 Sesame Street");
        tiSettlementRequest.setBillingCity("Grand Rapids");
        tiSettlementRequest.setBillingState("MI");
        tiSettlementRequest.setBillingZip("12345");
        tiSettlementRequest.setBillingPhone("777-555-1234");
        tiSettlementRequest.setBillingEMail("test@test.com");
        tiSettlementRequest.setBillingCountry("US");

        return tiSettlementRequest;
    }
}