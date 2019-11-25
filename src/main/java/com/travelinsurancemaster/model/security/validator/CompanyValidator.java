package com.travelinsurancemaster.model.security.validator;

import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.services.CompanyService;
import com.travelinsurancemaster.services.security.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CompanyValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(CompanyValidator.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) { return clazz.equals(Company.class); }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        Company company = (Company) target;
        validateName(errors, company);
    }

    private void validateName(Errors errors, Company company) {
        if (StringUtils.isBlank(company.getName())) {
            errors.reject("name.empty", "Company name cannot be empty");
        }
    }
}