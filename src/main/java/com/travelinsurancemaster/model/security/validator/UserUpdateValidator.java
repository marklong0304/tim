package com.travelinsurancemaster.model.security.validator;

import com.travelinsurancemaster.model.dto.UserInfo;
import com.travelinsurancemaster.model.dto.validator.PercentValidator;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.CommissionService;
import com.travelinsurancemaster.services.security.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * Created by Chernov Artur on 09.06.15.
 */

@Component
public class UserUpdateValidator implements Validator, PercentValidator {

    private static final Logger log = LoggerFactory.getLogger(UserUpdateValidator.class);

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        User user = (User) target;
        validatePercentValues(errors, user);
        validateEmptyRoles(errors, user);
        validateUniqueEmail(errors, user);
    }

    public void validateRoles(User user, Errors errors) {
        User dbUser = userService.getUserByEmail(user.getEmail());
        if(user.isCompanyManager() && dbUser.getCompany() != null && dbUser.getCompany().getCompanyManager() != null && !dbUser.getCompany().getCompanyManager().getId().equals(user.getId())) {
            errors.rejectValue("roles", null, "Company Manager already exists for this company");
        }
    }

    private void validateUniqueEmail(Errors errors, User user) {
        User storedUser = userService.getUserByEmail(user.getEmail());
        if (storedUser != null && !Objects.equals(storedUser.getId(), user.getId())) {
            errors.rejectValue("email", null, "User with this email already exists.");
        }
    }

    private void validateEmptyRoles(Errors errors, User user) {
        if (CollectionUtils.isEmpty(user.getRoles())) {
            errors.rejectValue("roles", null, "Roles cannot be empty");
        }
    }

    public void validateUserInfo(Object target, Errors errors) {
        log.debug("Validating {}", target);
        User user = (User) target;
        validateName(errors, user);
    }

    private void validatePercentValues(Errors errors, User user) {
        UserInfo userInfo = user.getUserInfo();
        if (user.getUserInfo().isCompany()) {
            validatePercents(errors, userInfo.getPercentInfo(), userInfo.getPercentType(), "userInfo.percentInfo");
        }
    }

    private void validateName(Errors errors, User user) {
        if (!user.getName().matches("[A-Z][a-zA-Z]*([ '-][a-zA-Z]+)*")) {
            errors.rejectValue("name", null, "Name can only contain Latin letters and start with an uppercase letter");
        }
    }

    @Override
    public CommissionService getCommissionService() {
        return commissionService;
    }
}
