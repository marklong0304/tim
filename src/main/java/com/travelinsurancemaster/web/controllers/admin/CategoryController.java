package com.travelinsurancemaster.web.controllers.admin;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.CategoryReduceResult;
import com.travelinsurancemaster.model.dto.Group;
import com.travelinsurancemaster.model.dto.Subcategory;
import com.travelinsurancemaster.model.dto.json.JsonResponse;
import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.model.dto.system.validator.SystemSettingsValidator;
import com.travelinsurancemaster.model.dto.validator.CategoryValidator;
import com.travelinsurancemaster.model.dto.validator.SubcategoryValidator;
import com.travelinsurancemaster.services.CategoryService;
import com.travelinsurancemaster.services.GroupService;
import com.travelinsurancemaster.services.SubcategoryService;
import com.travelinsurancemaster.services.system.SystemSettingsService;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Chernov Artur on 27.08.15.
 */

@Controller
@Scope(value = "request")
@RequestMapping(value = "/admin/categories")
public class CategoryController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    private final GroupService groupService;

    private final CategoryValidator categoryValidator;

    private final SystemSettingsService systemSettingsService;

    private final SystemSettingsValidator systemSettingsValidator;

    private final SubcategoryValidator subcategoryValidator;

    private final SubcategoryService subcategoryService;

    public CategoryController(CategoryService categoryService, GroupService groupService, CategoryValidator categoryValidator,
                              SystemSettingsService systemSettingsService, SystemSettingsValidator systemSettingsValidator,
                              SubcategoryValidator subcategoryValidator, SubcategoryService subcategoryService) {
        this.categoryService = categoryService;
        this.groupService = groupService;
        this.categoryValidator = categoryValidator;
        this.systemSettingsService = systemSettingsService;
        this.systemSettingsValidator = systemSettingsValidator;
        this.subcategoryValidator = subcategoryValidator;
        this.subcategoryService = subcategoryService;
    }

    private void setNavigation(Category category, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/admin/categories", "Categories");
        if (category != null && category.getId() != null) {
            map.put("/admin/categories/edit/" + String.valueOf(category.getId()), category.getName());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/admin/categories");
    }

    private List<Group> groups;

    private List<Category> categories;

    @ModelAttribute("groups")
    public List<Group> getGroups() {
        return groups;
    }

    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categories;
    }

    @PostConstruct
    public void init() {
        groups = groupService.getAll();
        categories = categoryService.getAllFillCanDelete();
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String getList(Model model) {
        model.addAttribute("settings", systemSettingsService.getDefault());
        return "admin/category/list";
    }

    @RequestMapping(value = {"edit/{categoryId}", "edit"}, method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String edit(@PathVariable Map<String, String> pathVariables, Model model) {
        Optional<Category> category = Optional.empty();
        if (pathVariables.containsKey("categoryId")) {
            category = Optional.ofNullable(categoryService.getFillCanDelete(NumberUtils.toLong(pathVariables.get("categoryId"))));
        }
        if (!category.isPresent()) {
            category = Optional.ofNullable(new Category());
        }
        setNavigation(category.get(), model);
        model.addAttribute("category", category.get());
        model.addAttribute("categoryReduceResult", CategoryReduceResult.CATEGORIES_FOR_REDUCE);
        model.addAttribute("subcategory", new Subcategory());
        return "admin/category/edit";
    }

    @RequestMapping(value = {"edit/{categoryId}", "edit"}, method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    @CacheEvict(value = {CacheConfig.CATEGORIES_CACHE}, allEntries = true)
    public String edit(@Valid @ModelAttribute("category") Category category,
                       BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Processing category update: category={}, bindingResult = {}", category, bindingResult);
        categoryValidator.validate(category, bindingResult);
        if (bindingResult.hasErrors()) {
            setNavigation(category, model);
            model.addAttribute("categoryReduceResult", CategoryReduceResult.CATEGORIES_FOR_REDUCE);
            model.addAttribute("subcategory", new Subcategory());
            return "admin/category/edit";
        }
        try {
            category = categoryService.save(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        setNavigation(category, model);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/admin/categories/edit/" + category.getId();
    }

    @RequestMapping(value = "/delete/{categoryId}",method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    @CacheEvict(value = {CacheConfig.CATEGORIES_CACHE}, allEntries = true)
    public String deleteMenu(@PathVariable("categoryId") Long categoryId, RedirectAttributes redirectAttributes) {
        log.debug("Delete category by id={} action", categoryId);
        redirectAttributes.addFlashAttribute("success", true);
        if (categoryService.getFillCanDelete(categoryId).isCanDelete()) {
            categoryService.delete(categoryId);
            categoryService.reSort();
        }
        return "redirect:/admin/categories";
    }

    @RequestMapping(method = RequestMethod.POST, params = {"planDescriptionCategory"})
    @Secured("ROLE_ADMIN")
    public String savePlanDescriptionCategory(@Valid @ModelAttribute("settings") SystemSettings settings,
                                              BindingResult bindingResult, Model model) {
        log.debug("Processing category update: system settings={}, bindingResult = {}", settings, bindingResult);
        model.addAttribute("activeTab", 2);
        systemSettingsValidator.validateCategories(settings, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/category/list";
        }
        try {
            settings = systemSettingsService.updatePlanDescriptionCategories(settings);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        if (settings != null) {
            model.addAttribute("settings", settings);
            model.addAttribute("successDescription", true);
            return "admin/category/list";
        } else {
            model.addAttribute("errorDescription", true);
            return "admin/category/list";
        }
    }

    @RequestMapping(value = "/up/{categoryId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String upCategory(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.get(categoryId);
        Category prevCategory = categoryService.getByFilterOrder(category.getFilterOrder() - 1);
        categoryService.swap(prevCategory, category);
        return "redirect:/admin/categories";
    }

    @RequestMapping(value = "/down/{categoryId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String downCategory(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.get(categoryId);
        Category nextCategory = categoryService.getByFilterOrder(category.getFilterOrder() + 1);
        categoryService.swap(nextCategory, category);
        return "redirect:/admin/categories";
    }

    @RequestMapping(value = "/subcategory/create/", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    @CacheEvict(value = {CacheConfig.CATEGORIES_CACHE}, allEntries = true)
    @ResponseBody
    public JsonResponse createSubcategory(@Valid @ModelAttribute("subcategory") Subcategory subcategory, BindingResult bindingResult) {
        subcategoryValidator.validate(subcategory, bindingResult);
        if (bindingResult.hasErrors()) {
            JsonResponse jsonResponse = new JsonResponse(false, bindingResult.getAllErrors());
            return jsonResponse;
        }
        subcategoryService.save(subcategory);
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/subcategory/edit/{subcategoryId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public Subcategory editSubcategory(@PathVariable("subcategoryId") Long subcategoryId) {
        Subcategory subcategory = subcategoryService.getSubcategoryById(subcategoryId);
        return subcategory;
    }

    @RequestMapping(value = "/subcategory/delete/{subcategoryId}", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConfig.CATEGORIES_CACHE}, allEntries = true)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String deleteSubcategory(@PathVariable("subcategoryId") Long subcategoryId) {
        Subcategory subcategory = subcategoryService.getSubcategoryById(subcategoryId);
        if (subcategory == null) {
            return "redirect:/404";
        }
        subcategoryService.delete(subcategory);
        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "/subcategory/up/{subcategoryId}", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConfig.CATEGORIES_CACHE}, allEntries = true)
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> upSubcategory(@PathVariable("subcategoryId") Long subcategoryId) {
        Optional<Subcategory> subcategory = Optional.ofNullable(subcategoryService.getSubcategoryById(subcategoryId));
        if (!subcategory.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        subcategoryService.up(subcategory.get());
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @RequestMapping(value = "/subcategory/down/{subcategoryId}", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConfig.CATEGORIES_CACHE}, allEntries = true)
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> downSubcategory(@PathVariable("subcategoryId") Long subcategoryId) {
        Optional<Subcategory> subcategory = Optional.ofNullable(subcategoryService.getSubcategoryById(subcategoryId));
        if (!subcategory.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        subcategoryService.down(subcategory.get());
        return ResponseEntity.ok().build();
    }

}
