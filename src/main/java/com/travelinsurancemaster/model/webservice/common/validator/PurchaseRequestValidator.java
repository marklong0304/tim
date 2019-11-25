package com.travelinsurancemaster.model.webservice.common.validator;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.util.ValidationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.YEARS;

/**
 * Created by Chernov Artur on 16.04.15.
 */


@Component
public class PurchaseRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PurchaseRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PurchaseRequest purchaseRequest = (PurchaseRequest) target;

        if (purchaseRequest.getTravelers().size() == 0) {
            errors.rejectValue(QuoteRequestConstants.TRAVELERS, null, "Incorrect count of travelers");
        }
        CreditCard creditCard = purchaseRequest.getCreditCard();
        if (creditCard == null) {
            errors.rejectValue("creditCard", null, "Credit card couldn't be null");
        } else {
            if (StringUtils.isEmpty(creditCard.getCcAddress())) {
                errors.rejectValue("creditCard.ccAddress", null, "Credit card address is required!");
            }
            if (creditCard.getCcAddress() != null && !creditCard.getCcAddress().matches("^[a-zA-Z0-9 '\\.,\\-/\\(\\)#]*$")) {
                errors.rejectValue("travelers[0].beneficiary", null, "Address can only contain Latin letters and allowed symbols!");
            }
            if (StringUtils.isEmpty(creditCard.getCcCity())) {
                errors.rejectValue("creditCard.ccCity", null, "Credit card city is required!");
            }
            if (StringUtils.isEmpty(creditCard.getCcName())) {
                errors.rejectValue("creditCard.ccName", null, "Cardholder name is required!");
            }
            if (creditCard.getCcCountry() == null) {
                errors.rejectValue("creditCard.ccCountry", null, "Credit card country may not be empty");
            } else if (creditCard.getCcStateCode() == null &&
                    (creditCard.getCcCountry() == CountryCode.US || creditCard.getCcCountry() == CountryCode.CA)) {
                errors.rejectValue("creditCard.ccStateCode", null, "Credit card state may not be empty");
            }
            if (StringUtils.isEmpty(creditCard.getCcZipCode())) {
                errors.rejectValue("creditCard.ccZipCode", null, "Credit card zip code may not be empty");
            }
            if (creditCard.getCcNumber() == null) {
                errors.rejectValue("creditCard.ccNumber", null, "Credit card number may not be null");
            } else {
                int cardDigitsAmount = String.valueOf(creditCard.getCcNumber()).length();
                if (Objects.isNull(creditCard.getTokenCcNumber())) {
                    if (creditCard.getCcType() == CardType.VISA && cardDigitsAmount != 16 && cardDigitsAmount != 13 && cardDigitsAmount != 19) {
                        errors.rejectValue("creditCard.ccNumber", null, "Visa card number length consists of 13, 16 or 19 digits!");
                    } else if (creditCard.getCcType() == CardType.MasterCard && cardDigitsAmount != 16) {
                        errors.rejectValue("creditCard.ccNumber", null, "MasterCard card number length consists of 16 digits!");
                    } else if (creditCard.getCcType() == CardType.AmericanExpress && cardDigitsAmount != 15) {
                        errors.rejectValue("creditCard.ccNumber", null, "AmericanExpress card number length consists of 15 digits!");
                    } else if (creditCard.getCcType() == CardType.Discover && cardDigitsAmount != 16 && cardDigitsAmount != 19) {
                        errors.rejectValue("creditCard.ccNumber", null, "Discover card number length consists of 16 or 19 digits!");
                    } else if (creditCard.getCcType() == CardType.Diners && cardDigitsAmount < 14 && cardDigitsAmount > 16) {
                        errors.rejectValue("creditCard.ccNumber", null, "Diners card number length consists of 14, 15 or 16 digits!");
                    }
                }
            }
        }
        validateTravelers(errors, purchaseRequest);
        validateZipCode(errors, purchaseRequest);
        validateBeneficiary(errors, purchaseRequest);
        validateTripType(errors, purchaseRequest);
    }

    private void validateTripType(Errors errors, PurchaseRequest purchaseRequest) {
        if (purchaseRequest.getTripTypes() == null) {
            errors.rejectValue("tripTypes", null, "Trip type is required!");
        }
    }

    private void validateBeneficiary(Errors errors, PurchaseRequest purchaseRequest) {
        if (purchaseRequest.getTravelers().size() == 0){
            return;
        }
        BeneficiaryType beneficiaryType = purchaseRequest.getProduct().getPolicyMeta().getVendor().getBeneficiaryType();
        GenericTraveler genericTraveler = purchaseRequest.getTravelers().get(0);
        String beneficiary = genericTraveler.getBeneficiary();
        switch (beneficiaryType) {
            case NOT_SUPPORTED:
                break;
            case SINGLE_BENEFICIARY_OPTIONAL:
                break;
            case SINGLE_BENEFICIARY:
                if (StringUtils.isBlank(beneficiary)) {
                    errors.rejectValue("travelers[0].beneficiary", null, "Beneficiary name is required!");
                }
                if (!beneficiary.matches("^[a-zA-Z]*([ '-][a-zA-Z]+)*$")) {
                    errors.rejectValue("travelers[0].beneficiary", null, "Beneficiary name can only contain Latin letters!");
                }
                if (genericTraveler.getBeneficiaryRelation() == null) {
                    errors.rejectValue("travelers[0].beneficiaryRelation", null, "Beneficiary relation is required!");
                }
                break;
            case BENEFICIARY_PER_TRAVELER_OPTIONAL:
                break;
            case BENEFICIARY_PER_TRAVELER:
                int i = 0;
                for (GenericTraveler traveler : purchaseRequest.getTravelers()) {
                    if (StringUtils.isBlank(traveler.getBeneficiary())) {
                        errors.rejectValue("travelers[" + i + "].beneficiary", null, "Beneficiary name is required!");
                    }
                    if (!beneficiary.matches("^[a-zA-Z]*([ '-][a-zA-Z]+)*$")) {
                        errors.rejectValue("travelers[" + i + "].beneficiary", null, "Beneficiary name can only contain Latin letters!");
                    }
                    if (traveler.getBeneficiaryRelation() == null) {
                        errors.rejectValue("travelers[" + i + "].beneficiaryRelation", null, "Beneficiary relation is required!");
                    }
                    i++;
                }
                break;
        }
    }

    public void validateCreditCardType(Errors errors, PurchaseRequest purchaseRequest, Collection<CardType> unsupportedCardTypes) {
        if (purchaseRequest != null && purchaseRequest.getCreditCard() != null && unsupportedCardTypes != null
                && unsupportedCardTypes.contains(purchaseRequest.getCreditCard().getCcType())) {
            errors.rejectValue("creditCard.ccType", null, "Credit card is not supported");
        }
    }

    public void validateEmail(Errors errors, User userByEmail) {
        //#59577: we permit purchase without login for existed emails
        /*User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser == null && userByEmail != null) {
            errors.rejectValue("email", null, "The email already exists in the system, please login or use another email.");
        }*/
    }

    public void validateTravelers(Errors errors, PurchaseRequest purchaseRequest) {
        LocalDate currentDate = LocalDate.now();
        int primaryCount = 0;
        int i = 0;
        for (GenericTraveler traveler : purchaseRequest.getTravelers()) {
            if (!traveler.getFirstName().matches("[a-zA-Z]*([ '-][a-zA-Z]+)*")) {
                errors.rejectValue("travelers[" + i + "].firstName", null, "First name can only contain latin letters");
            }
            if (!traveler.getLastName().matches("[a-zA-Z]*([ '-][a-zA-Z]+)*")) {
                errors.rejectValue("travelers[" + i + "].lastName", null, "Last name can only contain latin letters");
            }
            if (Objects.isNull(traveler.getAge()) || traveler.getAge() < 0) {
                errors.rejectValue("travelers[" + i + "].age", null, "Incorrect traveler age");
            }
            if (Objects.isNull(traveler.getBirthday()) || YEARS.between(traveler.getBirthday(), currentDate) != traveler.getAge()) {
                errors.rejectValue("travelers[" + i + "].birthday", null, "Incorrect traveler birthday");
            }
            if (StringUtils.isEmpty(traveler.getFirstName())) {
                errors.rejectValue("travelers[" + i + "].firstName", null, "First Name is required");
            }
            if (StringUtils.isEmpty(traveler.getLastName())) {
                errors.rejectValue("travelers[" + i + "].lastName", null, "Last Name is required");
            }

            primaryCount += traveler.isPrimary() ? 1 : 0;
            i++;
        }

        if (primaryCount > 1) {
            errors.rejectValue(QuoteRequestConstants.TRAVELERS, null, "Primary traveler should be just one");
        }
    }

    public void validateZipCode(Errors errors, PurchaseRequest purchaseRequest) {
        if (!ValidationUtils.isValidZipCode(purchaseRequest.getPostalCode(), purchaseRequest.getQuoteRequest().getResidentCountry().toString())) {
            errors.rejectValue("postalCode", null, "Invalid zip code");
        }
    }

    public void validateProduct(Errors errors, PurchaseRequest purchaseRequest, Product product) {
        if (product == null || purchaseRequest.getProduct() == null
                 || !Objects.equals(product.getTotalPrice().setScale(2), purchaseRequest.getProduct().getTotalPrice().setScale(2))
                ) {
            errors.rejectValue("product", null, "Sorry, the API request failed, please try again");
        }
    }

    public void validateDepositDate(Errors errors, LocalDate depositDate) {
        if (depositDate == null) {
            errors.rejectValue("product.policyMeta.requiredDepositDate", null, "Deposit date cannot be null");
        }
    }
}
