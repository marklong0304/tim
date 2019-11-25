package com.travelinsurancemaster.model.dto.cms.page.validator;

import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.services.cms.VendorPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * Created by Chernov Artur on 16.10.15.
 */

@Component
public class VendorPageValidator extends BasePageValidator implements Validator {
    private static final Logger log = LoggerFactory.getLogger(VendorPageValidator.class);

    @Autowired
    private VendorPageService vendorPageService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(VendorPage.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        VendorPage vendorPage = (VendorPage) target;
        validatePageCaption(vendorPage.getCaption(), errors);
        validateVendor(vendorPage, errors);
        validateUniqueName(vendorPage, errors);
    }

    private void validateVendor(VendorPage vendorPage, Errors errors) {
        if (vendorPage.getVendor() == null) {
            errors.rejectValue("vendor", null, "Vendor isn't selected!");
        } else {
            VendorPage vendorPageFromBase = vendorPageService.getVendorPageByVendor(vendorPage.getVendor());
            if (vendorPage.getId() != null && !Objects.equals(vendorPageFromBase.getId(), vendorPage.getId())) {
                errors.rejectValue("vendor", null, "Duplicate vendor");
            } else if (vendorPage.getId() == null && vendorPageFromBase != null) {
                errors.rejectValue("vendor", null, "Duplicate vendor");
            }
        }
    }

    private void validateUniqueName(VendorPage vendorPage, Errors errors) {
        VendorPage vendorPageFromBase = vendorPageService.getVendorPage(vendorPage.getName());
        if (vendorPageFromBase != null) {
            if (vendorPage.getId() != null && !Objects.equals(vendorPageFromBase.getId(), vendorPage.getId())) {
                errors.rejectValue("name", null, "Duplicate name");
            } else if (vendorPage.getId() == null) {
                errors.rejectValue("name", null, "Duplicate name");
            }
        }
    }
}
