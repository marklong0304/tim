package com.travelinsurancemaster.model.dto.cms.page.validator;

import com.travelinsurancemaster.model.dto.cms.page.Page;
import com.travelinsurancemaster.model.dto.cms.page.PageStatus;
import com.travelinsurancemaster.services.cms.MenuService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

/**
 * Created by Chernov Artur on 16.10.15.
 */

@Component
public class BasePageValidator {

    @Autowired
    private MenuService menuService;

    public void validatePageCaption(String caption, Errors errors) {
        Optional<String> pageCaption = Optional.ofNullable(caption);
        if (!pageCaption.isPresent() || StringUtils.isBlank(pageCaption.get())) {
            errors.rejectValue("caption", null, "Page caption cannot be empty");
        }
        if (pageCaption.isPresent() && !StringUtils.isBlank(pageCaption.get()) && !pageCaption.get().matches(".*[A-Za-z0-9].*")) {
            errors.rejectValue("caption", null, "Page caption should contain characters!");
        }
    }

    public void validatePageStatus(Page page, Errors errors) {
        if (page.getStatus() != PageStatus.PUBLISHED && CollectionUtils.isNotEmpty(menuService.getByPage(page))) {
            errors.rejectValue("status", null, "Page status cannot be changed, because has relation with menu element.");
        }
    }
}
