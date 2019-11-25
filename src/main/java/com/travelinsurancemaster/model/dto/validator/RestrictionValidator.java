package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.dto.Restriction;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.CalculatedRestrictionService;
import com.travelinsurancemaster.util.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by Chernov Artur on 24.08.15.
 */
@Component
public class RestrictionValidator implements Validator {

    @Autowired
    private CalculatedRestrictionService calculatedRestrictionService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Restriction.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Restriction restriction = (Restriction) target;
        if (restriction.getRestrictionType() == null) {
            errors.rejectValue("restrictionType", null, "Restriction Type is not selected");
        }
        if (restriction.getRestrictionType() != Restriction.RestrictionType.CALCULATE && restriction.getRestrictionPermit() == null) {
            errors.rejectValue("restrictionPermit", null, "Restriction Permit is not selected");
        }
        if (restriction.getRestrictionType() != null) {
            if (restriction.getRestrictionType() == Restriction.RestrictionType.CITIZEN
                    || restriction.getRestrictionType() == Restriction.RestrictionType.DESTINATION
                    || restriction.getRestrictionType() == Restriction.RestrictionType.RESIDENT) {
                if (CollectionUtils.isEmpty(restriction.getCountries()) && CollectionUtils.isEmpty(restriction.getStates())) {
                    errors.rejectValue(null, null, "Countries or States should be selected");
                }
            }
            validateRestrictionRange(restriction, errors);
            if (restriction.getRestrictionType() == Restriction.RestrictionType.CALCULATE) {
                validateCalculatedRestrictions(restriction.getCalculatedRestrictions(), errors);
            }
        }
    }

    public void validateCalculatedRestrictions(String calculatedRestrictions, Errors errors) {
        validateCalculatedRestrictions(calculatedRestrictions, null, errors);
    }

    public void validateCalculatedRestrictions(String calculatedRestrictions, QuoteRequest quoteRequest, Errors errors) {
        if (StringUtils.isEmpty(calculatedRestrictions)) {
            errors.rejectValue("calculatedRestrictions", null, "Calculated restriction is empty!");
        } else {
            if (quoteRequest == null) {
                quoteRequest = ValidationUtils.getSimpleQuoteRequest();
            }
            if (!calculatedRestrictionService.isValid(quoteRequest, calculatedRestrictions, true)) {
                errors.rejectValue("calculatedRestrictions", null, "Groovy script is invalid!");
            }
        }
    }

    public void validateRestrictionRange(Restriction restriction, Errors errors) {
        if (restriction.getRestrictionType() == Restriction.RestrictionType.AGE
                || restriction.getRestrictionType() == Restriction.RestrictionType.TRIP_COST
                || restriction.getRestrictionType() == Restriction.RestrictionType.TRIP_COST_PER_TRAVELER
                || restriction.getRestrictionType() == Restriction.RestrictionType.LENGTH_OF_TRAVEL) {
            if (restriction.getMinValue() == null && restriction.getMaxValue() == null) {
                errors.rejectValue(null, null, "Min or Max value should be filled");
            } else if (restriction.getMinValue() != null && restriction.getMaxValue() != null && restriction.getMinValue() > restriction.getMaxValue()) {
                errors.rejectValue("minValue", null, "Min value must be less than or equal to Max value");
            }
        }
    }
}
