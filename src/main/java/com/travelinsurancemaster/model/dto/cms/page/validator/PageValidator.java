package com.travelinsurancemaster.model.dto.cms.page.validator;

import com.travelinsurancemaster.model.dto.cms.page.Page;
import com.travelinsurancemaster.services.cms.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * Created by Chernov Artur on 28.07.15.
 */

@Component
public class PageValidator extends BasePageValidator implements Validator {
    private static final Logger log = LoggerFactory.getLogger(PageValidator.class);

    @Autowired
    private PageService pageService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Page.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Page page = (Page) target;
        validatePageStatus(page, errors);
        validateUniqueName(page, errors);
        validatePageCaption(page.getCaption(), errors);
    }

    private void validateUniqueName(Page page, Errors errors) {
        Page pageFromBase = pageService.getPage(page.getName());
        if (pageFromBase != null) {
            if (page.getId() != null && !Objects.equals(pageFromBase.getId(), page.getId())) {
                errors.rejectValue("name", null, "Duplicate name");
            } else if (page.getId() == null) {
                errors.rejectValue("name", null, "Duplicate name");
            }
        }
    }

}
