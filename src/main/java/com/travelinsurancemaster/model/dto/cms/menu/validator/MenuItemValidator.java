package com.travelinsurancemaster.model.dto.cms.menu.validator;

import com.travelinsurancemaster.model.dto.cms.menu.MenuItem;
import com.travelinsurancemaster.model.dto.cms.menu.MenuWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

/**
 * Created by Chernov Artur on 28.07.15.
 */

@Component
public class MenuItemValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(MenuItemValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(MenuItem.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        MenuItem menu = (MenuItem) target;
    }

    public void validateMenuItem(MenuWrapper menuItem, Errors errors, int lvl) {
        log.debug("Validating {}", menuItem);
        Optional<String> url = Optional.ofNullable(menuItem.getUrl());
        if (lvl > 1) {
            if ((!url.isPresent() || url.get().isEmpty()) && menuItem.getChildren() == null) {
                errors.reject(null, menuItem.getTitle() + ": URL cannot be empty");
            }
            if (url.isPresent() && !url.get().isEmpty() && !url.get().matches("[\\\\/:\\.#a-zA-Z0-9-_ ]+")) {
                errors.reject(null, menuItem.getTitle() + ": Illegal URL name: " + url.get() + ". URL name can only contain letters, numbers and the following characters:  / : \\ . # - _ ");
            }
        } else {
            if (url.isPresent() && !url.get().isEmpty()) {
                errors.reject(null, menuItem.getTitle() + ": URL for not leaf of menu tree should be empty");
            }
        }
        Optional<String> title = Optional.ofNullable(menuItem.getTitle());
        if (!title.isPresent() || title.get().isEmpty()) {
            errors.rejectValue("title", null, "title cannot be empty");
        } else if (title.get().length() > 255) {
            errors.rejectValue("title", null, "length must be between 1 and 255");
        }

    }
}
