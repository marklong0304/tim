package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.services.CommissionService;
import com.travelinsurancemaster.services.PolicyMetaPackageService;
import com.travelinsurancemaster.services.PolicyMetaService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Chernov Artur on 12.05.15.
 */

@Component
public class PolicyMetaValidator implements Validator, PercentValidator {
    private static final Logger log = LoggerFactory.getLogger(PolicyMetaValidator.class);

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PolicyMetaPackageService policyMetaPackageService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PolicyMeta.class);
    }

    @Override
    public CommissionService getCommissionService() {
        return commissionService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        PolicyMeta policyMeta = (PolicyMeta) target;
        validatePercents(errors, policyMeta.getPercentInfo(), policyMeta.getPercentType(), "percentInfo");
        PolicyMeta storedPolicyMeta = policyMetaService.getPolicyMetaByUniqueCode(policyMeta.getUniqueCode());

        if (policyMeta.getMinimalTripCost() == null) {
            errors.rejectValue("minimalTripCost", null, "Policy minimal trip cost is required");
        }

        if (policyMeta.getId() == null) {
            if (storedPolicyMeta != null) {
                errors.rejectValue("uniqueCode", null, "Policy unique code should be UNIQUE");
            }
        } else if (storedPolicyMeta != null) {
            if (!Objects.equals(storedPolicyMeta.getId(), policyMeta.getId())) {
                errors.rejectValue("uniqueCode", null, "Policy unique code should be UNIQUE");
            }
        }
    }

    public void validatePolicyMetaCode(PolicyMetaCode policyMetaCode, Errors errors) {
        validatePolicyMetaCode(policyMetaCode, errors, null);
    }

    public void validatePolicyMetaCode(PolicyMetaCode policyMetaCode, Errors errors, String fieldPreffix) {
        if (StringUtils.isEmpty(policyMetaCode.getCode())) {
            errors.rejectValue(fieldPreffix != null ? fieldPreffix + "code" : "code", null, "Policy meta code is required!");
        }
        if (StringUtils.isEmpty(policyMetaCode.getName())) {
            errors.rejectValue(fieldPreffix != null ? fieldPreffix + "name" : "name", null, "Policy meta name is required!");
        }
    }

    public void validatePolicyMetaPackage(PolicyMetaPackage policyMetaPackage, Errors errors) {
        if (StringUtils.isEmpty(policyMetaPackage.getCode())) {
            errors.rejectValue("code", null, "Policy meta package code is required!");
        }
        if (StringUtils.isEmpty(policyMetaPackage.getName())) {
            errors.rejectValue("name", null, "Policy meta name is required!");
        }

        PolicyMetaPackage existingPolicyMetaPackage = this.policyMetaPackageService.findByCode(policyMetaPackage.getPolicyMeta(), policyMetaPackage.getCode());
        if(existingPolicyMetaPackage != null && !existingPolicyMetaPackage.getId().equals(policyMetaPackage.getId())){
            errors.rejectValue("code", null, "Policy meta package code should be unique!");
        }


        List<PolicyMetaPackageValue> policyMetaPackageValues = policyMetaPackage.getPolicyMetaPackageValues();
        if(!CollectionUtils.isEmpty(policyMetaPackageValues)){
            Set<Long> policyMetaCategories = new HashSet<>();
            for(PolicyMetaPackageValue policyMetaPackageValue : policyMetaPackageValues){
                PolicyMetaCategory policyMetaCategory = policyMetaPackageValue.getPolicyMetaCategory();
                if(policyMetaCategories.contains(policyMetaCategory.getId())){
                    errors.rejectValue(null, null, "There are two or more values with the same category '" + policyMetaCategory.getCategory().getName() + "'");
                    break;
                }
                policyMetaCategories.add(policyMetaCategory.getId());
            }
        }
    }
}
