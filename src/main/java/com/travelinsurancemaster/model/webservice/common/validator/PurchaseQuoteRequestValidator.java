package com.travelinsurancemaster.model.webservice.common.validator;

import com.travelinsurancemaster.model.dto.purchase.PurchaseQuoteRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by ritchie on 2/18/16.
 */
@Component
public class PurchaseQuoteRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PurchaseQuoteRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PurchaseQuoteRequest purchaseQuoteRequest = (PurchaseQuoteRequest) target;
        ValidationUtils.rejectIfEmpty(errors, "residentCountryStatePair", null, "Residence is required!");
        validateTravelDatesRange(purchaseQuoteRequest, errors);
    }

    private void validateTravelDatesRange(PurchaseQuoteRequest purchaseQuoteRequest, Errors errors) {
        if (purchaseQuoteRequest.getReturnDate() == null || purchaseQuoteRequest.getDepartDate() == null) {
            return;
        }
        if (purchaseQuoteRequest.getDepartDate().compareTo(purchaseQuoteRequest.getReturnDate()) > 0) {
            errors.rejectValue("purchaseQuoteRequest.returnDate", null, "Return date must be later than depart date");
        }
    }
}
