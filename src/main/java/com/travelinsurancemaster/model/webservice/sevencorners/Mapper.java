package com.travelinsurancemaster.model.webservice.sevencorners;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.clients.SevenCornersClient;
import com.travelinsurancemaster.util.DateUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by ritchie on 8/18/15.
 */
@Component
public class Mapper {

    private static final String PAYMENT_TYPE = "CreditCard";

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DecimalFormat optionFormat = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.US));

    public SCQuoteRequest createQuoteRequest(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {

        //Quote
        SCQuoteRequest scQuoteRequest = new SCQuoteRequest();
        scQuoteRequest.setQuoteIdentifier(UUID.randomUUID().toString());
        scQuoteRequest.setQuoteRequestCount(1);

        //Policy requests
        List<PolicyQuoteRequest> policyQuoteRequests = new ArrayList<>();

        //Policy request
        PolicyQuoteRequest policyQuoteRequest = new PolicyQuoteRequest();
        policyQuoteRequest.setProductGroupId(Long.valueOf(policyMetaCode.getCode()));
        //Set effective date to depart date for medical plans and the current date for regular plans
        if(SevenCornersClient.LIAISON_TRAVEL_ECONOMY_CODE.equals(policyMetaCode.getCode())
                || SevenCornersClient.LIAISON_TRAVEL_CHOICE_CODE.equals(policyMetaCode.getCode())
                || SevenCornersClient.LIAISON_TRAVEL_ELITE_CODE.equals(policyMetaCode.getCode())) {
            policyQuoteRequest.setEffectiveDate(quoteRequest.getDepartDate());
        } else {
            policyQuoteRequest.setEffectiveDate(DateUtil.getLocalDateNow(quoteRequest.getTimezoneOffset()));
        }
        policyQuoteRequest.setExpirationDate(quoteRequest.getReturnDate());

        long tripPeriod = quoteRequest.getTripLength();
        String yesValue = SevenCornersClient.ROUNDTRIP_CHOICE_CODE.equals(policyMetaCode.getCode()) ? "Yes" : "Selected";
        String noValue = SevenCornersClient.ROUNDTRIP_CHOICE_CODE.equals(policyMetaCode.getCode()) ? "No" : "Declined";

        //Fields
        List<Field> fields = new ArrayList<>();
        addField(fields, "InitialTripDepositDate_FLD", formatDate(quoteRequest.getDepositDate()));
        addField(fields, "TripStartDate_FLD", formatDate(quoteRequest.getDepartDate()));
        addField(fields, "TripEndDate_FLD", formatDate(quoteRequest.getReturnDate()));
        addField(fields, "CFAR_BND", quoteRequest.getCategoryValue(CategoryCodes.CANCEL_FOR_ANY_REASON) != null ? yesValue : noValue);
        String rentalCarValue = getCurrencyOptionValue(quoteRequest, CategoryCodes.RENTAL_CAR, "Declined");
        addField(fields, "RCarD_BND", rentalCarValue);
        if(!"Declined".equals(rentalCarValue)) {
            addField(fields, "RCarD_BND_DAY", String.valueOf(quoteRequest.getRentalCarLength()));
        }
        addField(fields, "FliAcc_BND", getCurrencyOptionValue(quoteRequest, CategoryCodes.FLIGHT_ONLY_AD_AND_D, "Declined"));
        addField(fields, "LstGlf_BND", "Declined");
        addField(fields, "LstSki_BND", "Declined");
        addField(fields, "CWR_BND", quoteRequest.getCategoryValue(CategoryCodes.CANCEL_FOR_WORK_REASONS) != null ? yesValue : noValue);
        addField(fields, "DestinationCountry_FLD", iso2ToIso3CountryCode(quoteRequest.getDestinationCountry().name()));
        addField(fields, "PassportIssuerCountry_FLD", iso2ToIso3CountryCode(quoteRequest.getCitizenCountry().name()));
        addField(fields, "HomeCountryResidence_FLD", iso2ToIso3CountryCode(quoteRequest.getResidentCountry().name()));

        if(SevenCornersClient.WANDER_FREQUENT_TRAVELER_CODE.equals(policyMetaCode.getCode())) {

            //For Wander Frequent Traveler plans
            addField(fields, "HasPrimaryInsurance_FLD", "false");
            addField(fields, "HSC_BND", quoteRequest.getCategoryValue(CategoryCodes.HAZARDOUS_SPORTS) != null ? "Yes" : "No");
            addField(fields, "PlnTyp_BND", tripPeriod <= 30 ? "Plan A" : "Plan B");
            addField(fields, "InOut_BND", quoteRequest.getIncludesUS() != null && quoteRequest.getIncludesUS() ? "Yes" : "No");

        } else if(SevenCornersClient.LIAISON_TRAVEL_ECONOMY_CODE.equals(policyMetaCode.getCode())
                || SevenCornersClient.LIAISON_TRAVEL_CHOICE_CODE.equals(policyMetaCode.getCode())
                || SevenCornersClient.LIAISON_TRAVEL_ELITE_CODE.equals(policyMetaCode.getCode())) {

            //For Liaison Travel plans
            addField(fields, "DedPgm_BND", getCurrencyOptionValue(quoteRequest, CategoryCodes.MEDICAL_DEDUCTIBLE, "$0"));
            addField(fields, "MMPgm_BND", getCurrencyOptionValue(quoteRequest, CategoryCodes.EMERGENCY_MEDICAL, "$50,000"));
            addField(fields, "HSC_BND", quoteRequest.getCategoryValue(CategoryCodes.HAZARDOUS_SPORTS) != null ? "Selected" : "Declined");
            addField(fields, "InOut_BND", quoteRequest.getIncludesUS() != null && quoteRequest.getIncludesUS() ? "Yes" : "No");
        }

        policyQuoteRequest.setFields(fields);

        BigDecimal travelerNumber = BigDecimal.valueOf(quoteRequest.getTravelers().size());
        BigDecimal tripCost = quoteRequest.getTripCost();
        BigDecimal tripCostPerTraveler = tripCost.divide(travelerNumber, 2, RoundingMode.HALF_UP);
        BigDecimal tripCostFirstTraveler = tripCost.subtract(tripCostPerTraveler.multiply(travelerNumber.subtract(BigDecimal.ONE)));

        //Person fields
        List<PersonField> personFields = new ArrayList<>();
        int num = 1;
        for(GenericTraveler traveler : quoteRequest.getTravelers()) {
            PersonField personField = new PersonField();
            personField.setPersonIdentifier("Person" + num);
            fields = new ArrayList<>();
            addField(fields, "TrpCos_BND", String.valueOf(num == 1 ? tripCostFirstTraveler : tripCostPerTraveler));
            personField.setFields(fields);
            personFields.add(personField);
            policyQuoteRequest.setPersonFields(personFields);
            num++;
        }
        policyQuoteRequests.add(policyQuoteRequest);

        scQuoteRequest.setPolicyQuoteRequests(policyQuoteRequests);

        //Set persons
        List<Person> persons = new ArrayList<>();
        num = 1;
        for(GenericTraveler traveler : quoteRequest.getTravelers()) {
            Person person = new Person();
            person.setClientPersonIdentifier("Person" + num);
            person.setFirstName("First");
            person.setLastName("Last");
            person.setDateOfBirth(traveler.getBirthdaySafe());
            person.setRelationship(num == 1 ? "Primary" : "Secondary");
            List<Phone> phones = new ArrayList<>();
            Phone phone = new Phone();
            phone.setPhoneNumber("123-456-7890");
            phone.setPhoneType("Home");
            phones.add(phone);
            person.setPhones(phones);
            List<Email> emails = new ArrayList<>();
            Email email = new Email();
            email.setEmailAddress("example.email@sevencorners.com");
            emails.add(email);
            person.setEmails(emails);
            persons.add(person);
            num++;
        }
        scQuoteRequest.setPersons(persons);

        //Set primary member addresses
        List<PrimaryMemberAddress> primaryMemberAddresses = new ArrayList<>();
        PrimaryMemberAddress primaryMemberAddress = new PrimaryMemberAddress();
        primaryMemberAddress.setAddressLine1("Address");
        primaryMemberAddress.setCity("City");
        primaryMemberAddress.setCountryCode(iso2ToIso3CountryCode(quoteRequest.getResidentCountry().name()));
        if(quoteRequest.getResidentState() != null) {
            primaryMemberAddress.setState(quoteRequest.getResidentState().name());
        }
        primaryMemberAddresses.add(primaryMemberAddress);
        scQuoteRequest.setPrimaryMemberAddresses(primaryMemberAddresses);

        return scQuoteRequest;
    }

    public SCPurchaseRequest createPurchaseRequest(PurchaseRequest purchaseRequest, InsuranceMasterApiProperties.SevenCorners sevenCornersProperties) {

        CreditCard creditCard = purchaseRequest.getCreditCard();
        SCPurchaseRequest scPurchaseRequest = new SCPurchaseRequest();
        scPurchaseRequest.setQuoteIdentifier(purchaseRequest.getProduct().getQuoteIdentifier());
        scPurchaseRequest.setQuoteVersion(purchaseRequest.getProduct().getQuoteVersion());
        scPurchaseRequest.setAgreedToDisclaimerText(true);
        scPurchaseRequest.setCanSendPromotionalMaterials(true);
        scPurchaseRequest.setAgentNumber(sevenCornersProperties.getAgentNumber());
        PaymentInformation paymentInformation = new PaymentInformation();
        paymentInformation.setPaymentType(PAYMENT_TYPE);
        paymentInformation.setBillingName(creditCard.getCcName());
        paymentInformation.setAddress1(creditCard.getCcAddress());
        paymentInformation.setAddress2(creditCard.getCcAddressLine2());
        paymentInformation.setCity(creditCard.getCcCity());
        paymentInformation.setState(creditCard.getCcStateCode() != null ? creditCard.getCcStateCode().name() : "");
        paymentInformation.setCountry(creditCard.getCcCountry().name());
        paymentInformation.setPostalCode(creditCard.getCcZipCode());
        String creditCardType;
        switch (creditCard.getCcType()) {
            case VISA:
                creditCardType = "Visa";
                break;
            case MasterCard:
                creditCardType = "Master Card";
                break;
            case AmericanExpress:
                creditCardType = "American Express";
                break;
            case Discover:
                creditCardType = "Discover";
                break;
            case Diners:
                creditCardType = "Diners";
                break;
            default:
                creditCardType = "";
        }
        paymentInformation.setCreditCardType(creditCardType);
        paymentInformation.setCreditCardNumber(String.valueOf(creditCard.getCcNumber()));
        paymentInformation.setCvv2(creditCard.getCcCode());
        paymentInformation.setCreditCardExpirationDate(creditCard.getCcExpMonth() + "/" + creditCard.getCcExpYear());
        paymentInformation.setPaymentDate(LocalDate.now());
        scPurchaseRequest.setPaymentInformation(paymentInformation);
        return scPurchaseRequest;
    }

    private void addField(List<Field> fields, String code, String value) {
        Field field = new Field();
        field.setCode(code);
        field.setValue(value);
        fields.add(field);
    }

    private String formatDate(LocalDate date) {
        return date != null ? dateFormat.format(date) : null;
    }

    private String iso2ToIso3CountryCode(String iso2CountryCode) {
        String iso3CountryCode = null;
        if(iso2CountryCode != null) {
            Locale locale = new Locale("", iso2CountryCode);
            iso3CountryCode = locale.getISO3Country();
        }
        return iso3CountryCode;
    }

    private String getCurrencyOptionValue(QuoteRequest quoteRequest, String optionName, String defaultValue) {
        String formattedOptionValue = defaultValue;
        String optionValue = quoteRequest.getCategoryValue(optionName);
        if(optionValue != null) {
            Double value = Double.valueOf(optionValue);
            if (value != null) {
                formattedOptionValue = "$" + optionFormat.format(value);
            }
        }
        return formattedOptionValue;
    }
}