package com.travelinsurancemaster.model.webservice.common.validator;

import com.travelinsurancemaster.model.security.User;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by ritchie on 4/23/15.
 */
public class PasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (!user.getPassword().equals(user.getRepeatPassword())) {
            errors.rejectValue("repeatPassword", null, "Passwords do not match");
        }
    }
}
