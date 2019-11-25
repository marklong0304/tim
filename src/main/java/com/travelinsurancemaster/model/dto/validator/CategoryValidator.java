package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.services.CategoryService;
import com.travelinsurancemaster.util.FakeConditionalCategoryUtil;
import com.travelinsurancemaster.util.GroovyScriptUtil;
import com.travelinsurancemaster.util.ValidationUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Set;

/**
 * Created by Chernov Artur on 28.08.15.
 */

@Component
public class CategoryValidator implements Validator {
    private static final Logger log = LoggerFactory.getLogger(CategoryValidator.class);

    @Autowired
    private CategoryService categoryService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Category.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        Category category = (Category) target;
        if (category.getId() == null && categoryService.getByCode(category.getCode()) != null) {
            errors.rejectValue("code", null, "Category code should be UNIQUE");
        }
        if (category.getCode().isEmpty()) {
            errors.rejectValue("code", null, "Category code is required");
        }
        if (category.getName().isEmpty()) {
            errors.rejectValue("name", null, "Category name is required");
        }
        if (category.getType() == Category.CategoryType.CONDITIONAL) {
            if (StringUtils.isEmpty(category.getCategoryCondition())) {
                errors.rejectValue("categoryCondition", null, "Category condition is required");
            } else {
                validateCategoryCondition(category.getCategoryCondition(), errors);
            }
        }
    }

    private void validateCategoryCondition(String categoryCondition, Errors errors) {
        if (!isCategoriesExistsInCondition(categoryCondition)) {
            errors.rejectValue("categoryCondition", null, "Category from category condition is not exists!");
        } else {
            Binding binding = new Binding();
            GroovyShell groovyShell = new GroovyShell(binding);
            binding.setVariable("util", new FakeConditionalCategoryUtil(ValidationUtils.getSimpleQuoteRequest()));
            try {
                groovyShell.evaluate(categoryCondition);
            } catch (GroovyRuntimeException e) {
                errors.rejectValue("categoryCondition", null, "Category condition compilation is failed!");
            }
        }
    }

    private boolean isCategoriesExistsInCondition(String categoryCondition) {
        Set<String> categoryCodesFromCondition = GroovyScriptUtil.getCategoriesCodesFromCondition(categoryCondition);
        if (CollectionUtils.isEmpty(categoryCodesFromCondition)) {
            return false;
        }
        List<Category> categories = categoryService.getAllCategoriesWithoutConditional();
        for (String categoryCode : categoryCodesFromCondition) {
            boolean codeExists = false;
            for (Category category : categories) {
                if (categoryCode.equals(category.getCode())) {
                    codeExists = true;
                    break;
                }
            }
            if (!codeExists) {
                return false;
            }
        }
        return true;
    }

}
