package com.travelinsurancemaster.model.dto.cms.page.validator;

import com.travelinsurancemaster.model.dto.cms.page.FilingClaimPage;
import com.travelinsurancemaster.services.cms.FilingClaimPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * Created by Chernov Artur on 09.12.2015.
 */

@Component
public class FilingClaimPageValidator extends BasePageValidator implements Validator {

    @Autowired
    private FilingClaimPageService filingClaimPageService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(FilingClaimPage.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FilingClaimPage filingClaimPage = (FilingClaimPage) target;
        validatePageCaption(filingClaimPage.getCaption(), errors);
        validateVendor(filingClaimPage, errors);
        validateUniqueName(filingClaimPage, errors);
    }

    private void validateVendor(FilingClaimPage filingClaimPage, Errors errors) {
        FilingClaimPage filingClaimPageFromBase = filingClaimPageService.getFilingClaimPageByVendorPage(filingClaimPage.getVendorPage());
        if (filingClaimPageFromBase == null) {
            return;
        }
        if (filingClaimPage.getId() != null && !Objects.equals(filingClaimPageFromBase.getId(), filingClaimPage.getId())) {
            errors.rejectValue("vendorPage", null, "Duplicate vendor page");
        } else if (filingClaimPage.getId() == null && filingClaimPageFromBase != null) {
            errors.rejectValue("vendorPage", null, "Duplicate vendor page");
        }
    }

    private void validateUniqueName(FilingClaimPage filingClaimPage, Errors errors) {
        FilingClaimPage filingClaimPageFromBase = filingClaimPageService.getFilingClaimPage(filingClaimPage.getName());
        if (filingClaimPageFromBase != null) {
            if (filingClaimPage.getId() != null && !Objects.equals(filingClaimPageFromBase.getId(), filingClaimPage.getId())) {
                errors.rejectValue("name", null, "Duplicate name");
            } else if (filingClaimPage.getId() == null) {
                errors.rejectValue("name", null, "Duplicate name");
            }
        }
    }
}
