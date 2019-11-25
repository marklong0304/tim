package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.cms.page.CategoryContent;
import com.travelinsurancemaster.services.CategoryService;
import com.travelinsurancemaster.services.cms.CategoryContentService;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by Chernov Artur on 20.10.15.
 */

@Controller
@Scope(value = "request")
@RequestMapping(value = "/cms/category_content")
public class CategoryContentController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CategoryContentController.class);

    @Autowired
    private CategoryContentService categoryContentService;

    @Autowired
    private CategoryService categoryService;

    @PostConstruct
    public void init() {
    }

    private void setNavigation(CategoryContent categoryContent, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/cms/category_content", "Insurance Terms");
        if (categoryContent != null && categoryContent.getId() != null) {
            map.put("/cms/category_content/edit/" + String.valueOf(categoryContent.getId()), categoryContent.getName());
        } else {
            if (categoryContent != null && categoryContent.getCategory() != null) {
                map.put("/cms/category_content/create/" + categoryContent.getCode(), "Create");
            } else if (categoryContent != null) {
                map.put("/cms/category_content/create/custom", "Create");
            }
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/cms/category_content");
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String getCategoryContentList(Model model) {
        model.addAttribute("categoryContentList", categoryContentService.getCategoryContentListForceRepairSort());
        addUnusedCategories(model);
        return "cms/page/content/category/list";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, params = {"sort"})
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String sort(Model model) {
        model.addAttribute("categoryContentList", categoryContentService.sort());
        return "cms/page/content/category/list";
    }

    @RequestMapping(value = {"/create/{categoryCode}"}, method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createCategoryContent(@PathVariable("categoryCode") String categoryCode, Model model) {
        CategoryContent categoryContent = new CategoryContent();
        if (!"custom".equals(categoryCode)) {
            Category category = categoryService.getByCode(categoryCode);
            if (category == null) {
                return "redirect:/404";
            }
            if (categoryContentService.getByCode(categoryCode) != null) {
                return "redirect:/404";
            }
            categoryContent.setCategory(category);
            categoryContent.setName(category.getName());
            categoryContent.setCode(category.getCode());
        }
        model.addAttribute("categoryContent", categoryContent);
        setNavigation(categoryContent, model);
        return "cms/page/content/category/edit";
    }

    @RequestMapping(value = "/edit/{categoryContentId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editCategoryContent(@PathVariable("categoryContentId") Long categoryContentId, Model model) {
        CategoryContent categoryContent = categoryContentService.getCategoryContent(categoryContentId);
        if (categoryContent == null) {
            return "redirect:/404";
        }
        setNavigation(categoryContent, model);
        model.addAttribute("categoryContent", categoryContent);
        return "cms/page/content/category/edit";
    }

    @RequestMapping(value = {"/edit/{categoryContentId}", "/edit"}, method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editPage(@Valid @ModelAttribute("categoryContent") CategoryContent categoryContent, BindingResult bindingResult, Model model) {
        if (StringUtils.isBlank(categoryContent.getContent().replaceAll("\\<.*?>", ""))) {
            bindingResult.reject(null, "Category content cannot be empty");
        }
        CategoryContent categoryContentFromBase = categoryContentService.getByCode(categoryContent.getCode());
        if (categoryContent.getId() == null && categoryContentFromBase != null) {
            bindingResult.reject(null, "Duplicate category code");
        }
        if (categoryContent.getId() != null && categoryContentFromBase != null && !Objects.equals(categoryContentFromBase.getId(), categoryContent.getId())) {
            bindingResult.reject(null, "Duplicate category code");
        }
        if (bindingResult.hasErrors()) {
            setNavigation(categoryContent, model);
            return "cms/page/content/category/edit";
        }
        try {
            categoryContent = categoryContentService.saveCategoryContent(categoryContent);
        } catch (ObjectOptimisticLockingFailureException e) {
            bindingResult.reject("optimistic.locking.error");
            log.warn("Optimistic locking error occurred when trying to create the page", e);
            return "cms/page/content/category/edit";
        }
        addMessageSuccess("Saved", model);
        return editCategoryContent(categoryContent.getId(), model);
    }

    @RequestMapping(value = "/delete/{categoryContentId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String deleteCategoryContent(@PathVariable("categoryContentId") Long categoryContentId) {
        categoryContentService.deleteCategoryContent(categoryContentId);
        return "redirect:/cms/category_content";
    }

    @RequestMapping(value = "/up/{categoryContentId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String upCategory(@PathVariable("categoryContentId") Long id) {
        CategoryContent categoryContent = categoryContentService.getCategoryContent(id);
        CategoryContent prevCategoryContent = categoryContentService.getBySortOrder(categoryContent.getSortOrder() - 1);
        categoryContentService.swap(prevCategoryContent, categoryContent);
        return "redirect:/cms/category_content";
    }

    @RequestMapping(value = "/down/{policyMetaCategoryContentId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String downPolicyMetaCategory(@PathVariable("policyMetaCategoryContentId") Long id) {
        CategoryContent categoryContent = categoryContentService.getCategoryContent(id);
        CategoryContent nextCategoryContent = categoryContentService.getBySortOrder(categoryContent.getSortOrder() + 1);
        categoryContentService.swap(nextCategoryContent, categoryContent);
        return "redirect:/cms/category_content";
    }

    private void addUnusedCategories(Model model) {
        Set<Category> categorySet = new HashSet<>(categoryService.getAllCategoriesWithoutConditional());
        List<CategoryContent> categoryContentList = categoryContentService.getCategoryContentList();
        for (CategoryContent categoryContent : categoryContentList) {
            categorySet.remove(categoryContent.getCategory());
        }
        model.addAttribute("categories", new ArrayList<>(categorySet));
    }
}
