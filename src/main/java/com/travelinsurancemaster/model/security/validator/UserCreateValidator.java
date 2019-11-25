package com.travelinsurancemaster.model.security.validator;

import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.CompanyService;
import com.travelinsurancemaster.services.security.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Chernov Artur on 12.05.15.
 */

@Component
public class UserCreateValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(UserCreateValidator.class);
    private static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match";
    private static final String PASSWORD_MUST_BE_AT_LEAST_6_CHARACTERS_LONG = "Password must be at least 6 characters long.";
    private static final String PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_NUMBER = "Password must contain at least one number.";
    private static final String PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_UPPERCASE_LATIN_LETTER = "Password must contain at least one uppercase latin letter.";
    private static final String YOUR_PASSWORD_CANT_CONTAIN_SPACES = "Your password can't contain spaces.";

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        User user = (User) target;
        validatePasswords(errors, user);
        validateEmail(errors, user);
        validateName(errors, user);
    }

    public void validateWithoutEmail(Object target, Errors errors) {
        log.debug("Validating {}", target);
        User user = (User) target;
        validatePasswords(errors, user);
        validateName(errors, user);
    }

    public void validateCompanyUser(Object target, Errors errors) {
        log.debug("Validating {}", target);
        User user = (User) target;
        validatePasswords(errors, user);
        validateName(errors, user);
        User dbUser = userService.getUserByEmail(user.getEmail());
        if(dbUser != null && dbUser.getCompany() != null) {
            errors.reject("company.exists", "User with this email already has a company");
        }
    }

    private void validateName(Errors errors, User user) {
        if (StringUtils.isBlank(user.getName())) {
            errors.reject("name.empty", "User name cannot be empty");
        }
        if (StringUtils.isBlank(user.getUserInfo().getLastName())) {
            errors.reject("lastName.empty", "Last name cannot be empty");
        } else if (!user.getUserInfo().getLastName().matches("[a-zA-Z]*([ '-][a-zA-Z]+)*")) {
            errors.reject("lastName.incorrect", "Last name can only contain latin letters");
        }
        if (!user.getName().matches("[a-zA-Z]*([ '-][a-zA-Z]+)*")) {
            errors.reject("name.incorrect", "First name can only contain latin letters");
        }
    }

    public List<String> validatePasswordRules(String password) {
        log.debug("Validating password rules");
        List<String> errors = new ArrayList<>();
        if (!password.matches("^.*(?=.{6,}).*$")) {
            errors.add(PASSWORD_MUST_BE_AT_LEAST_6_CHARACTERS_LONG);
        }
        /*if (!password.matches("^.*(?=.*[0-9]).*$")) {
            errors.add(PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_NUMBER);
        }
        if (!password.matches("^.*(?=.*[A-Z]).*$")) {
            errors.add(PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_UPPERCASE_LATIN_LETTER);
        }*/
        if (!password.matches("(?=\\S+$).*$")) {
            errors.add(YOUR_PASSWORD_CANT_CONTAIN_SPACES);
        }
        return errors;
    }

    public List<String> validatePasswordsMatch(String password, String repeatPassword) {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(password) || StringUtils.isBlank(repeatPassword)) {
            errors.add("empty password not allowed");
        }
        if (!password.equals(repeatPassword)) {
            errors.add(PASSWORDS_DO_NOT_MATCH);
        }
        return errors;
    }

    private void validatePasswords(Errors errors, User user) {
        List<String> errorList = validatePasswordsMatch(user.getPassword(), user.getRepeatPassword());
        for (String error : errorList) {
            errors.reject("password.no_match", error);
        }
        errorList = validatePasswordRules(user.getPassword());
        for (String error : errorList) {
            errors.reject("password.simple", error);
        }
    }

    private void validateEmail(Errors errors, User user) {
        if (userService.getUserByEmail(user.getEmail()) != null) {
            errors.reject("email.exists", "User with this email already exists");
        }
    }

    public void validateRole(Set<Role> roles, Errors errors) {
        if (CollectionUtils.isEmpty(roles)) {
            errors.reject("roles.empty", "User role is empty!");
        }
    }
}