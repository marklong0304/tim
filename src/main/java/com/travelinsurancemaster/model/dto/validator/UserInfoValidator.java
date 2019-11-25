package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.dto.UserInfo;
import com.travelinsurancemaster.util.ValidationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by Chernov Artur on 12.05.15.
 */

@Component
public class UserInfoValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(UserInfoValidator.class);

    private static final String LAST_NAME_ERROR = "Last name can only contain Latin letters and start with an uppercase letter";
    private static final String ADDRESS_ERROR = "Address can't be empty";
    private static final String CITY_ERROR = "City can't be empty";
    private static final String COUNTRY_ERROR = "Country can't be empty";
    private static final String ZIP_CODE_ERROR = "Invalid zip code";
    private static final String RESIDENCE_ERROR = "Resident country is required";

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserInfo.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        UserInfo userInfo = (UserInfo) target;
        if (!userInfo.isCompany()) {
            validateLastName(errors, userInfo);
        }
        validateAddress(errors, userInfo);
        validateCity(errors, userInfo);
        validateCountry(errors, userInfo);
        validateZipCode(errors, userInfo);
        validatePhone(errors, userInfo);
        validateCountryStatePair(errors, userInfo);
    }

    public void validateWithoutRequired(Object target, Errors errors) {
        log.debug("Validating {}", target);
        UserInfo userInfo = (UserInfo) target;
        if (!userInfo.isCompany() && !userInfo.getLastName().equals(StringUtils.EMPTY)) {
            validateLastName(errors, userInfo);
        }
        if (!userInfo.getZipCode().equals(StringUtils.EMPTY) && !userInfo.getCountryStatePair().equals(StringUtils.EMPTY)) {
            validateZipCode(errors, userInfo);
        }
        if (!userInfo.getPhone().equals(StringUtils.EMPTY)) {
            validatePhone(errors, userInfo);
        }
        if (!userInfo.getCountryStatePair().equals(StringUtils.EMPTY)) {
            validateCountryStatePair(errors, userInfo);
        }
    }

    private void validateLastName(Errors errors, UserInfo userInfo) {
        if (!userInfo.getLastName().matches("[A-Z][a-zA-Z]*([ '-][a-zA-Z]+)*")) {
            errors.rejectValue("userInfo.lastName", null, LAST_NAME_ERROR);
        }
    }

    private void validateAddress(Errors errors, UserInfo userInfo) {
        if(userInfo.getAddress().isEmpty()) {
            errors.rejectValue("userInfo.address", null, ADDRESS_ERROR);
        }
    }

    private void validateCity(Errors errors, UserInfo userInfo) {
        if(userInfo.getCity().isEmpty()) {
            errors.rejectValue("userInfo.city", null, CITY_ERROR);
        }
    }

    private void validateCountry(Errors errors, UserInfo userInfo) {
        if(userInfo.getCountry() == null) {
            errors.rejectValue("userInfo.country", null, COUNTRY_ERROR);
        }
    }

    private void validateZipCode(Errors errors, UserInfo userInfo) {
        if (!ValidationUtils.isValidZipCode(userInfo.getZipCode(), userInfo.getCountryStatePair())) {
            errors.rejectValue("userInfo.zipCode", null, ZIP_CODE_ERROR);
        }
    }

    private void validatePhone(Errors errors, UserInfo userInfo) {
        ValidationUtils.validatePhone(errors, userInfo.getPhone(), "userInfo.phone");
    }

    private void validateCountryStatePair(Errors errors, UserInfo userInfo) {
        if (StringUtils.isEmpty(userInfo.getCountryStatePair())) {
            errors.rejectValue("userInfo.countryStatePair", null, RESIDENCE_ERROR);
        }
    }

}
