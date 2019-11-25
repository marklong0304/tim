package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.Subcategory;
import com.travelinsurancemaster.services.CategoryService;
import com.travelinsurancemaster.services.SubcategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by ritchie on 7/6/16.
 */
@Component
public class SubcategoryValidator implements Validator {

    @Autowired
    private SubcategoryService subcategoryService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Subcategory.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Subcategory subcategory = (Subcategory) target;
        if (StringUtils.isEmpty(subcategory.getSubcategoryName())) {
            errors.rejectValue("subcategoryName", null, "Subcategory name is required");
        }
        if (StringUtils.isEmpty(subcategory.getSubcategoryCode())) {
            errors.rejectValue("subcategoryCode", null, "Subcategory code is required");
        } else {
            if (subcategory.getId() == null && subcategoryService.getSubcategory(subcategory.getSubcategoryCode(), subcategory.getCategory().getId()) != null) {
                errors.rejectValue("subcategoryCode", null, "Subcategory code should be unique");
            }
        }
        if (StringUtils.isBlank(subcategory.getTemplate())){
            errors.rejectValue("template", null, "Subcategory template is required");
        }
        Category category = categoryService.get(subcategory.getCategory().getId());
        if (category.getType() == Category.CategoryType.CONDITIONAL) {
            errors.rejectValue("category", null, "Conditional category can't contain subcategories");
        }
    }
}
