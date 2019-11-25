package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.services.CategoryService;
import com.travelinsurancemaster.services.GroupService;

/**
 * Created by ritchie on 8/4/16.
 */
// todo: add CancellationConditionalCategoryUtil usage after applying new design.
public class CancellationConditionalCategoryUtil extends ConditionalCategoryUtil {

    private boolean hasCancellationCategory;
    private GroupService groupService;
    private CategoryService categoryService;

    public CancellationConditionalCategoryUtil(GroupService groupService, CategoryService categoryService) {
        this.groupService = groupService;
        this.categoryService = categoryService;
    }

    public boolean isHasCancellationCategory() {
        return hasCancellationCategory;
    }

    public void setHasCancellationCategory(boolean hasCancellationCategory) {
        this.hasCancellationCategory = hasCancellationCategory;
    }

    public GroupService getGroupService() {
        return groupService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Set hasCancellationCategory flag to true if condition category is from Cancellation group.
     */
    @Override
    public boolean has(String code, String type, String val) {
        Category category = categoryService.getByCode(code);
        if (GroupService.isCancellationGroup(category)) {
            this.setHasCancellationCategory(true);
        }
        return true;
    }
}
