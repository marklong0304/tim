package com.travelinsurancemaster.model.dto.cms.page.validator;

import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import com.travelinsurancemaster.services.cms.PolicyMetaCategoryContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

/**
* Created by ritchie on 11/23/15.
*/
@Component
public class PolicyMetaCategoryContentValidator implements Validator {

    @Autowired
    private PolicyMetaCategoryContentService policyMetaCategoryContentService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PolicyMetaCategoryContent.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PolicyMetaCategoryContent policyMetaCategoryContent = (PolicyMetaCategoryContent) target;
        List<PolicyMetaCategoryContent> policyMetaCategoryContentList;
        String name = policyMetaCategoryContent.getName();

        if (policyMetaCategoryContent.getCertificateText() == null
                || policyMetaCategoryContent.getCertificateText().isEmpty()) {
            errors.rejectValue("certificateText", null, "Certificate Text must be filled");
        }

        if (policyMetaCategoryContent.getPolicyMetaPageForCategory() != null) {
            return;
        } else if (policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() != null) {
            policyMetaCategoryContentList = policyMetaCategoryContentService.getPolicyMetaCustomCategoryContentByName(name, policyMetaCategoryContent.getPolicyMetaPageForCustomCategory().getId());
        } else if (policyMetaCategoryContent.getPolicyMetaPageForPlanInfo() != null) {
            policyMetaCategoryContentList = policyMetaCategoryContentService.getPolicyMetaPlanInfoContentByName(name, policyMetaCategoryContent.getPolicyMetaPageForPlanInfo().getId());
        } else if (policyMetaCategoryContent.getPolicyMetaPageForRestrictions() != null) {
            policyMetaCategoryContentList = policyMetaCategoryContentService.getPolicyMetaRestrictionsContentByName(name, policyMetaCategoryContent.getPolicyMetaPageForRestrictions().getId());
        } else {
            policyMetaCategoryContentList = policyMetaCategoryContentService.getPolicyMetaPackageContentListByName(name, policyMetaCategoryContent.getPolicyMetaPackage().getId());
        }
        validateUniqueName(policyMetaCategoryContent, policyMetaCategoryContentList, errors);
    }

    private void validateUniqueName(PolicyMetaCategoryContent policyMetaCategoryContentCurrent, List<PolicyMetaCategoryContent> policyMetaCategoryContentList, Errors errors) {
        for (PolicyMetaCategoryContent policyMetaCategoryContent : policyMetaCategoryContentList) {
            if (policyMetaCategoryContent.getName().equals(policyMetaCategoryContentCurrent.getName()) &&
                    (!(policyMetaCategoryContent.getId() == null)
                            && !policyMetaCategoryContent.getId().equals(policyMetaCategoryContentCurrent.getId()))) {
                errors.rejectValue("name", null, "Duplicate name");
            }
            break;
        }
    }
}