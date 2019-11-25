package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by ritchie on 5/4/16.
 */
@Component
public class PolicyMetaCategoryValueValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PolicyMetaCategoryValue.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
    }

    public void validateCategoryValue(PolicyMetaCategoryValue changedPolicyMetaCategoryValue, PolicyMetaCategory policyMetaCategory, BindingResult bindingResult) {
        validateUniqueCategoryValue(changedPolicyMetaCategoryValue, policyMetaCategory, bindingResult);
        validateCategoryValueIfSingle(policyMetaCategory, bindingResult);
        validateMinMaxAge(changedPolicyMetaCategoryValue, bindingResult);
    }

    private void validateMinMaxAge(PolicyMetaCategoryValue changedPolicyMetaCategoryValue, BindingResult bindingResult) {
        if (changedPolicyMetaCategoryValue.getMaxAge() != null && changedPolicyMetaCategoryValue.getMinAge() != null &&
                changedPolicyMetaCategoryValue.getMaxAge() < changedPolicyMetaCategoryValue.getMinAge()) {
            bindingResult.rejectValue(null, "-1", "Minimum age must be less than or equal to maximum age");
        }
    }

    private void validateUniqueCategoryValue(PolicyMetaCategoryValue changedPolicyMetaCategoryValue, PolicyMetaCategory policyMetaCategory, BindingResult bindingResult) {
        int count = 0;
        for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategory.getValues()) {
            if (policyMetaCategoryValue.getValue().equals(changedPolicyMetaCategoryValue.getValue())) {
                count++;
            }
        }
        // value is added to map to store duplicate and return error.
        if (count > 1) {
            bindingResult.rejectValue("value", "-1", "Value must be unique!");
        }
    }

    private void validateCategoryValueIfSingle(PolicyMetaCategory policyMetaCategory, BindingResult bindingResult) {
        if (policyMetaCategory.getValues().size() == 1 && StringUtils.isBlank(policyMetaCategory.getValues().get(0).getValue())) {
            bindingResult.rejectValue("value", "-1", "Single category value cannot be empty");
        }
    }
}
