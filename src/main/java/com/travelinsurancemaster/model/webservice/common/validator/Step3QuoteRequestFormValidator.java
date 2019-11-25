package com.travelinsurancemaster.model.webservice.common.validator;

import com.travelinsurancemaster.model.webservice.common.QuoteRequestConstants;
import com.travelinsurancemaster.model.webservice.common.forms.Step3QuoteRequestForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

/**
 * Created by Chernov Artur on 27.04.15.
 */
public class Step3QuoteRequestFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Step3QuoteRequestForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Step3QuoteRequestForm form = (Step3QuoteRequestForm) target;
        if (form.isPreExistingMedicalAndCancellation()) {
            LocalDate currentDate = LocalDate.now();
            if(form.getDepositDate() != null) {
                if(form.getDepositDate().compareTo(currentDate) > 0)
                errors.rejectValue(QuoteRequestConstants.DEPOSIT_DATE, null, "Deposit date cannot be in the past");
            }
            if(form.getPaymentDate() != null && form.getDepositDate() != null)
                if(form.getDepositDate().compareTo(form.getPaymentDate()) > 0)
                    errors.rejectValue(QuoteRequestConstants.DEPOSIT_DATE, null, "Final payment date must be later than Deposit date");

        }
    }
}