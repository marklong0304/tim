package com.travelinsurancemaster.model.dto.system.validator;

import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.model.dto.validator.PercentValidator;
import com.travelinsurancemaster.services.CommissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chernov Artur on 25.08.15.
 */

@Component
public class SystemSettingsValidator implements Validator, PercentValidator {
    private static final Logger log = LoggerFactory.getLogger(SystemSettingsValidator.class);

    @Autowired
    private CommissionService commissionService;

    @Override
    public CommissionService getCommissionService() {
        return commissionService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SystemSettings.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        SystemSettings systemSettings = (SystemSettings) target;
        validatePercents(errors, systemSettings.getDefaultLinkPercentInfo(), systemSettings.getDefaultLinkPercentType(), "defaultLinkPercentInfo");
    }

    public void validateCategories(SystemSettings settings, Errors errors) {
        Set<Long> categoriesIds = new HashSet<>();
        categoriesIds.add(settings.getPlanDescriptionCategory1().getId());
        categoriesIds.add(settings.getPlanDescriptionCategory2().getId());
        categoriesIds.add(settings.getPlanDescriptionCategory3().getId());
        categoriesIds.add(settings.getPlanDescriptionCategory4().getId());
        categoriesIds.add(settings.getPlanDescriptionCategory5().getId());
        categoriesIds.add(settings.getPlanDescriptionCategory6().getId());
        if(categoriesIds.size() != 6){
            errors.reject(null, "All categories must be different");
        }
    }
}
