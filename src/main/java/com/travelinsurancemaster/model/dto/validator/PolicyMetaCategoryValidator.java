package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Chernov Artur on 31.08.15.
 */

@Component
public class PolicyMetaCategoryValidator implements Validator {
    private static final Logger log = LoggerFactory.getLogger(PolicyMetaCategoryValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PolicyMetaCategory.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        PolicyMetaCategory policyMetaCategory = (PolicyMetaCategory) target;

        if (CollectionUtils.isEmpty(policyMetaCategory.getValues())) {
            errors.reject(null, "Values cannot be empty");
        }
        if (policyMetaCategory.getValues().size() == 1 && StringUtils.isBlank(policyMetaCategory.getValues().get(0).getValue())) {
            errors.reject(null, "Single category value cannot be empty");
        }
        if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.SIMPLE
                && policyMetaCategory.getValues().size() > 1) {
            errors.reject(null, "Simple type can contain only 1 value");
        }
        if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE
                && policyMetaCategory.getValues().size() < 2) {
            errors.reject(null, "Up Sale type must contain greater than 1 value");
        }
        List<PolicyMetaCategoryValue> policyMetaCategoryValues = policyMetaCategory.getValues();
        if (policyMetaCategoryValues.size() > 1) {
            HashSet<String> valuesHash = new HashSet<>();
            for (PolicyMetaCategoryValue value : policyMetaCategoryValues) {
                valuesHash.add(value.getValue());
            }
            if (valuesHash.size() != policyMetaCategory.getValues().size()) {
                errors.reject(null, "Value should be unique");
            }
        }
        validateMinMaxAge(policyMetaCategoryValues, errors);
    }

    private void validateMinMaxAge(List<PolicyMetaCategoryValue> policyMetaCategoryValues, Errors errors) {
        policyMetaCategoryValues.stream().filter(value -> value.getMaxAge() != null && value.getMinAge() != null && value.getMaxAge() < value.getMinAge()).forEach(value -> {
            errors.reject(null, "Minimum age must be less than or equal to maximum age");
        });
        policyMetaCategoryValues.stream().filter(value -> value.getMaxAge() != null && value.getMinAge() != null && (
                value.getMinAge() < 1 || value.getMinAge() > 119 || value.getMaxAge() < 1 || value.getMaxAge() > 119)).forEach(value -> {
            errors.reject(null, "Minimum and maximum ages should be in range from 1 to 119");
        });
    }
}
