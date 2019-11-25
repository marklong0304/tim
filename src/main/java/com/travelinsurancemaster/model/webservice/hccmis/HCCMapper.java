package com.travelinsurancemaster.model.webservice.hccmis;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.clients.HCCMedicalInsuranceServicesClient;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ritchie on 8/18/15.
 */
@Component
public class HCCMapper {

    public HCCPurchaseRequest createQuoteRequest(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {

        HCCPurchaseRequest hccPurchaseRequest = new HCCPurchaseRequest();

        hccPurchaseRequest.setUSDest(quoteRequest.getDestinationCountry() == CountryCode.US ? "1" : "0");
        hccPurchaseRequest.setCoverageBeginDt(quoteRequest.getDepartDate());
        hccPurchaseRequest.setCoverageEndDt(quoteRequest.getReturnDate());
        hccPurchaseRequest.setPrimaryHomeCountry(quoteRequest.getResidentCountry().name());
        hccPurchaseRequest.setDestinationList(Arrays.asList(quoteRequest.getDestinationCountry().name()));
        hccPurchaseRequest.setBeneficiary("Other");
        hccPurchaseRequest.setPhysicallyLocatedState(HCCMedicalInsuranceServicesClient.PHYSICALLY_LOCATED_STATES.contains(quoteRequest.getResidentState()) ? "1" : "0");
        hccPurchaseRequest.setPhysicallyLocatedCountry(HCCMedicalInsuranceServicesClient.PHYSICALLY_LOCATED_COUNTRIES.contains(quoteRequest.getResidentState()) ? "1" : "0");
        String policyMax = quoteRequest.getCategoryValue("emergency-medical");
        hccPurchaseRequest.setPolicyMax(policyMax != null ? policyMax : "50000");
        String deductable = quoteRequest.getCategoryValue("medical-deductible");
        hccPurchaseRequest.setDeductible(deductable != null ? deductable : "0");
        hccPurchaseRequest.setAppName(getAppName(policyMetaCode));
        hccPurchaseRequest.setMailAddress1("Address");
        hccPurchaseRequest.setMailCity("City");
        hccPurchaseRequest.setMailCountry("Country");
        hccPurchaseRequest.setMailState("ST");
        hccPurchaseRequest.setMailZip("12345");
        hccPurchaseRequest.setPhone("123-456-7890");
        hccPurchaseRequest.setEmailAddress("example.email@hccmis.com");

        String citizenship = quoteRequest.getCitizenCountry().name();
        List<Applicant> applicantList = quoteRequest.getTravelers().stream()
                .map(traveler -> new Applicant("Joe", "", "Black", traveler.getBirthdaySafe(), HCCMedicalInsuranceServicesClient.DEFAULT_GENDER, citizenship, ""))
                .collect(Collectors.toList());
        hccPurchaseRequest.setApplicantList(applicantList);

        CreditCard creditCard = new CreditCard();
        creditCard.setCardExpirationMonth(12);
        creditCard.setCardExpirationYear(2099);
        creditCard.setCardHolderName("Joe Black");
        creditCard.setCardHolderAddress1("Address");
        creditCard.setCardHolderAddress2("");
        creditCard.setCardHolderCity("City");
        creditCard.setCardHolderCountry("Country");
        creditCard.setCardHolderState("ST");
        creditCard.setCardHolderZip("12345");
        creditCard.setCardNumber("4111111111111111");
        creditCard.setCardSecurityCode("123");
        creditCard.setPaymentMethod("Visa");

        Transaction transaction = new Transaction(0.0);

        creditCard.setTransaction(transaction);

        hccPurchaseRequest.setCreditCard(creditCard);

        return hccPurchaseRequest;
    }

    public HCCPurchaseRequest createPurchaseRequest(PurchaseRequest purchaseRequest) {

        QuoteRequest quoteRequest = purchaseRequest.getQuoteRequest();

        HCCPurchaseRequest hccPurchaseRequest = new HCCPurchaseRequest();

        Optional<GenericTraveler> primaryTravelerOptional = purchaseRequest.getTravelers().stream().filter(t -> t.isPrimary()).findFirst();
        GenericTraveler primaryTraveler = primaryTravelerOptional.isPresent() ? primaryTravelerOptional.get() : null;

        hccPurchaseRequest.setUSDest(quoteRequest.getDestinationCountry() == CountryCode.US ? "1" : "0");
        hccPurchaseRequest.setCoverageBeginDt(quoteRequest.getDepartDate());
        hccPurchaseRequest.setCoverageEndDt(quoteRequest.getReturnDate());
        hccPurchaseRequest.setPrimaryHomeCountry(quoteRequest.getResidentCountry().name());
        hccPurchaseRequest.setDestinationList(Arrays.asList(quoteRequest.getDestinationCountry().name()));
        hccPurchaseRequest.setBeneficiary(primaryTraveler.getBeneficiary());
        hccPurchaseRequest.setPhysicallyLocatedState(HCCMedicalInsuranceServicesClient.PHYSICALLY_LOCATED_STATES.contains(quoteRequest.getResidentState()) ? "1" : "0");
        hccPurchaseRequest.setPhysicallyLocatedCountry(HCCMedicalInsuranceServicesClient.PHYSICALLY_LOCATED_COUNTRIES.contains(quoteRequest.getResidentState()) ? "1" : "0");
        String policyMax = quoteRequest.getCategoryValue("emergency-medical");
        hccPurchaseRequest.setPolicyMax(policyMax != null ? policyMax : "50000");
        String deductable = quoteRequest.getCategoryValue("medical-deductible");
        hccPurchaseRequest.setDeductible(deductable != null ? deductable : "0");
        hccPurchaseRequest.setAppName(getAppName(purchaseRequest.getProduct().getPolicyMetaCode()));
        hccPurchaseRequest.setMailAddress1(purchaseRequest.getAddress());
        hccPurchaseRequest.setMailAddress2(purchaseRequest.getAddressLine2());
        hccPurchaseRequest.setMailCity(purchaseRequest.getCity());
        hccPurchaseRequest.setMailCountry(quoteRequest.getResidentCountry().name());
        hccPurchaseRequest.setMailState(quoteRequest.getResidentState().name());
        hccPurchaseRequest.setMailZip(purchaseRequest.getPostalCode());
        hccPurchaseRequest.setPhone(purchaseRequest.getPhone());
        hccPurchaseRequest.setEmailAddress(purchaseRequest.getEmail());

        String citizenship = quoteRequest.getCitizenCountry().name();
        List<Applicant> applicantList = purchaseRequest.getTravelers().stream()
                .map(traveler -> new Applicant(traveler, HCCMedicalInsuranceServicesClient.DEFAULT_GENDER, citizenship))
                .collect(Collectors.toList());
        hccPurchaseRequest.setApplicantList(applicantList);

        CreditCard creditCard = new CreditCard();
        creditCard.setCardExpirationMonth(Integer.parseInt(purchaseRequest.getCreditCard().getCcExpMonth()));
        creditCard.setCardExpirationYear(Integer.parseInt(purchaseRequest.getCreditCard().getCcExpYear()));
        creditCard.setCardHolderName(purchaseRequest.getCreditCard().getCcName());
        creditCard.setCardHolderAddress1(purchaseRequest.getCreditCard().getCcAddress());
        creditCard.setCardHolderAddress2(purchaseRequest.getCreditCard().getCcAddressLine2());
        creditCard.setCardHolderCity(purchaseRequest.getCreditCard().getCcCity());
        creditCard.setCardHolderCountry(purchaseRequest.getCreditCard().getCcCountry().name());
        creditCard.setCardHolderState(purchaseRequest.getCreditCard().getCcStateCode().name());
        creditCard.setCardHolderZip(purchaseRequest.getCreditCard().getCcZipCode());
        creditCard.setCardNumber(String.valueOf(purchaseRequest.getCreditCard().getCcNumber()));
        creditCard.setCardSecurityCode(purchaseRequest.getCreditCard().getCcCode());
        String paymentType = "";
        switch(purchaseRequest.getCreditCard().getCcType()) {
            case VISA:
                paymentType = "Visa";
                break;
            case MasterCard:
                paymentType = "MC";
                break;
            case AmericanExpress:
                paymentType = "AMEX";
                break;
            case Discover:
                paymentType = "Discover";
                break;
        }
        creditCard.setPaymentMethod(paymentType);

        Transaction transaction = new Transaction(purchaseRequest.getProduct().getTotalPrice().doubleValue());

        creditCard.setTransaction(transaction);

        hccPurchaseRequest.setCreditCard(creditCard);

        return hccPurchaseRequest;
    }

    private String getAppName(PolicyMetaCode policyMetaCode) {
        String appName = "";
        switch(policyMetaCode.getCode()) {
            case HCCMedicalInsuranceServicesClient.HCCMIS_ATLAS_TRAVEL:
                appName = "AT";
                break;
            case HCCMedicalInsuranceServicesClient.HCCMIS_ATLAS_PREMIUM:
                appName = "AP";
                break;
            case HCCMedicalInsuranceServicesClient.HCCMIS_ATLAS_ESSENTIAL:
                appName = "AE";
                break;
        }
        return appName;
    }
}