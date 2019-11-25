package com.travelinsurancemaster.model.webservice.common.validator;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequestConstants;
import com.travelinsurancemaster.model.webservice.common.forms.Step2QuoteRequestForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Created by Chernov Artur on 27.04.15.
 */
public class Step2QuoteRequestFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Step2QuoteRequestForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Step2QuoteRequestForm form = (Step2QuoteRequestForm) target;
        if (form.getTravelers().size() == 0) {
            errors.rejectValue(QuoteRequestConstants.TRAVELERS, null, "Incorrect count of travelers");
        }
        List<GenericTraveler> travelers = form.getTravelers();
        for (int i = 0; i < travelers.size(); i++) {
            // error for each traveler will be rejected:
            if (travelers.get(i).getAge() == null){
                errors.rejectValue("travelers[" + i + "].age" , null, "Invalid number or date format");
            } else if (travelers.get(i).getAgeInDays() < 1 || travelers.get(i).getAge() > 119) {
                errors.rejectValue("travelers[" + i + "].age" , null, "Age should be in range from 1 to 119");
            }
        }
        ValidationUtils.rejectIfEmpty(errors, QuoteRequestConstants.RESIDENT_COUNTRY, "error.residentCountry.required", "Resident country is required");
        if (form.getResidentCountry() == CountryCode.US || form.getResidentCountry() == CountryCode.CA) {
            ValidationUtils.rejectIfEmpty(errors, QuoteRequestConstants.RESIDENT_STATE, "error.residentState.required", "Resident state is required");
            if (form.getResidentState() != null && !(form.getResidentState().getCountryCode() == form.getResidentCountry())) {
                errors.rejectValue(QuoteRequestConstants.RESIDENT_STATE, null, "There is not this state in this country");
            }
        } else {
            if (form.getResidentState() != null) {
                errors.rejectValue(QuoteRequestConstants.RESIDENT_STATE, null, "Resident state must be empty");
            }
        }
        ValidationUtils.rejectIfEmpty(errors, QuoteRequestConstants.CITIZEN_COUNTRY, "error.citizenCountry.required", "Citizen country is required");
    }
}
