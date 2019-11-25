package com.travelinsurancemaster.model.dto.cms.page.validator;

import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.services.cms.PolicyMetaPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * Created by Chernov Artur on 19.10.15.
 */

@Component
public class PolicyMetaPageValidator extends BasePageValidator implements Validator {
    private static final Logger log = LoggerFactory.getLogger(PolicyMetaPageValidator.class);

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PolicyMetaPageValidator.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PolicyMetaPage policyMetaPage = (PolicyMetaPage) target;
        validatePageCaption(policyMetaPage.getCaption(), errors);
        validateUniqueName(policyMetaPage, errors);
    }

    private void validateUniqueName(PolicyMetaPage policyMetaPage, Errors errors) {
        if (policyMetaPage.getName() == null) {
            return;
        }
        PolicyMetaPage policyMetaPageFromBase = policyMetaPageService.getPolicyMetaPage(policyMetaPage.getName());
        if (policyMetaPageFromBase == null) {
            return;
        }
        if (policyMetaPage.getId() != null && !Objects.equals(policyMetaPageFromBase.getId(), policyMetaPage.getId())) {
            errors.rejectValue("name", null, "Duplicate name");
        } else if (policyMetaPage.getId() == null && policyMetaPageFromBase != null) {
            errors.rejectValue("name", null, "Duplicate name");
        }
    }

}
