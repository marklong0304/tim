package com.travelinsurancemaster.model.webservice.common.validator;

import com.travelinsurancemaster.model.webservice.common.QuoteRequestConstants;
import com.travelinsurancemaster.model.webservice.common.forms.Step1QuoteRequestForm;
import com.travelinsurancemaster.util.DateUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Chernov Artur on 27.04.15.
 */
public class Step1QuoteRequestFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Step1QuoteRequestForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Step1QuoteRequestForm form = (Step1QuoteRequestForm) target;
        ValidationUtils.rejectIfEmpty(errors, QuoteRequestConstants.DESTINATION_COUNTRY, "error.destinationCountry.required", "Destination country is required");
        LocalDate currentDate = DateUtil.getLocalDateNow(form.getTimezoneOffset());;
        if (form.getDepartDate().compareTo(currentDate) <= 0) {
            errors.rejectValue(QuoteRequestConstants.DEPART_DATE, null, "Departure date must be tomorrow or later");
        }
        if (form.getDepartDate().compareTo(form.getReturnDate()) > 0) {
            errors.rejectValue(QuoteRequestConstants.RETURN_DATE, null, "Return date must be later than depart date");
        }
        if (form.getTripCost().compareTo(BigDecimal.ZERO) < 0) {
            errors.rejectValue("tripCost", null, "The cost of trip must be positive");
        }
    }
}
