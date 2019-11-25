package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.services.CommissionService;
import com.travelinsurancemaster.services.VendorService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * Created by Chernov Artur on 12.05.15.
 */

@Component
public class VendorValidator implements Validator, PercentValidator {

    private static final Logger log = LoggerFactory.getLogger(VendorValidator.class);

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private VendorService vendorService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Vendor.class);
    }

    @Override
    public CommissionService getCommissionService() {
        return commissionService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Vendor vendor = (Vendor) target;
        log.debug("Validating {}", vendor.getName());
        validatePercents(errors, vendor.getPercentInfo(), vendor.getPercentType(), "percentInfo");
        Vendor storedVendor = vendorService.getByCode(vendor.getCode());
        if (StringUtils.isBlank(vendor.getName())) {
            errors.rejectValue("name", null, "Vendor name is required");
        }
        if (StringUtils.isBlank(vendor.getCode())) {
            errors.rejectValue("code", null, "Vendor code is required");
        }
        if (vendor.getId() == null) {
            if (storedVendor != null) {
                errors.rejectValue("code", null, "Vendor unique code should be UNIQUE");
            }
        } else if (storedVendor != null) {
            if (!Objects.equals(storedVendor.getId(), vendor.getId())) {
                errors.rejectValue("code", null, "Vendor unique code should be UNIQUE");
            }
        }
    }
}
