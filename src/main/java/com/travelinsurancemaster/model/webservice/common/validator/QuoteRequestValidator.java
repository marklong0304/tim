package com.travelinsurancemaster.model.webservice.common.validator;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequestConstants;
import com.travelinsurancemaster.util.DateUtil;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Artur Chernov on 15.04.15.
 */

@Component
public class QuoteRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return QuoteRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) throws NullPointerException {
        QuoteRequest quoteRequest = (QuoteRequest) target;
        if (quoteRequest.getDepartDate() == null) {
            errors.rejectValue(QuoteRequestConstants.DEPART_DATE, null, "Departure date is null!");
        } else {
            LocalDate currentDate = DateUtil.getLocalDateNow(quoteRequest.getTimezoneOffset());
            if (quoteRequest.getDepartDate().compareTo(currentDate) <= 0) {
                errors.rejectValue(QuoteRequestConstants.DEPART_DATE, null, "Departure date must be tomorrow or later");
            }
        }
        if (quoteRequest.getReturnDate() == null) {
            errors.rejectValue(QuoteRequestConstants.RETURN_DATE, null, "Return date is null!");
        } else {
            if (quoteRequest.getDepartDate().compareTo(quoteRequest.getReturnDate()) > 0) {
                errors.rejectValue(QuoteRequestConstants.RETURN_DATE, null, "Return date must be later than depart date");
            }
        }
        if (quoteRequest.getTripCost() == null) {
            errors.rejectValue("tripCost", null, "The cost is null!");
        } else {
            if (quoteRequest.getTripCost().compareTo(BigDecimal.ZERO) == -1) { //todo: check
                errors.rejectValue("tripCost", null, "The cost of trip must be positive");
            }
        }
        ValidationUtils.rejectIfEmpty(errors, QuoteRequestConstants.RESIDENT_COUNTRY, "error.residentCountry.required", "Resident country is required");
        if (quoteRequest.getResidentCountry() == CountryCode.US || quoteRequest.getResidentCountry() == CountryCode.CA) {
            ValidationUtils.rejectIfEmpty(errors, QuoteRequestConstants.RESIDENT_STATE, "error.residentState.required", "Resident state is required");
            if (quoteRequest.getResidentState() != null && !(quoteRequest.getResidentState().getCountryCode() == quoteRequest.getResidentCountry())) {
                errors.rejectValue(QuoteRequestConstants.RESIDENT_STATE, null, "There is not this state in this country");
            }
        } else {
            if (quoteRequest.getResidentState() != null) {
                errors.rejectValue(QuoteRequestConstants.RESIDENT_STATE, null, "Resident state must be empty");
            }
        }
        ValidationUtils.rejectIfEmpty(errors, QuoteRequestConstants.CITIZEN_COUNTRY, "error.citizenCountry.required", "Citizen country is required");
        ValidationUtils.rejectIfEmpty(errors, QuoteRequestConstants.DESTINATION_COUNTRY, "error.destinationCountry.required", "Destination country is required");
        if (quoteRequest.getTravelers().size() == 0) {
            errors.rejectValue(QuoteRequestConstants.TRAVELERS, null, "Incorrect count of travelers");
        }
        if (quoteRequest.getTravelers().isEmpty()) {
            errors.rejectValue(QuoteRequestConstants.TRAVELERS, null, "Travelers is null!");
        } else {
            List<GenericTraveler> travelers = quoteRequest.getTravelers();
            for (int i = 0; i < travelers.size(); i++) {
                GenericTraveler traveler = travelers.get(i);
                if (traveler.getAge() != null && (traveler.getAgeInDays() < 1 || traveler.getAge() > 119)) {
                    errors.rejectValue("travelers[" + i + "].age", null, "Age should be in range from 1 to 119");
                }
            }
        }
    }

}
