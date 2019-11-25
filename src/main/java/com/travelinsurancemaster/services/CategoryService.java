package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.GroupNames;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.cms.page.CategoryContent;
import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.repository.CategoryRepository;
import com.travelinsurancemaster.services.cache.CacheService;
import com.travelinsurancemaster.services.cms.CategoryContentService;
import com.travelinsurancemaster.services.system.SystemSettingsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 10.08.15.
 */


@Service
@Transactional
public class CategoryService {

    final static int MAX_POLICY_REASONS = 9;

    @Lazy
    @Autowired
    private CacheService cacheService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private CategoryContentService categoryContentService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private GroupService groupService;

    @PostConstruct
    private void init() {
        this.reSort();
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(new Sort(Sort.Direction.ASC, "filterOrder"));
    }

    /**
     * Conditional categories must be displayed only on result page.
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategoriesWithoutConditional() {
        return categoryRepository.findByTypeNotLike(Category.CategoryType.CONDITIONAL, new Sort(Sort.Direction.ASC, "filterOrder"));
    }

    @Transactional(readOnly = true)
    public List<Category> getAllDisplayedAsFilter() {
        return getAllCategories().stream().filter(Category::isDisplayAsFilter).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Category> getAllFillCanDelete() {
        List<Category> categories = getAllCategories();
        categories.forEach(c -> fillCanDelete(c));
        return categories;
    }

    @Transactional(readOnly = true)
    public Integer getMaxFilterOrder() {
        return categoryRepository.findFirstByOrderByFilterOrderDesc().getFilterOrder();
    }

    @Transactional(readOnly = true)
    public Category getByCode(String code) {
        return categoryRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Category get(Long id) {
        return categoryRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Category getFillCanDelete(Long id) {
        Optional<Category> category = Optional.ofNullable(get(id));
        category.ifPresent(c -> fillCanDelete(c));
        return category.orElseThrow(() -> new NullPointerException("Category not found"));
    }

    @Transactional(readOnly = true)
    public List<Category> getByCategoryType(Category.CategoryType type) {
        return categoryRepository.findByType(type);
    }

    /**
     * @return group category map for policy meta category matrix page
     */
    @Transactional(readOnly = true)
    public Map<String, List<Category>> getGroupCategoryMap() {
        Map<String, List<Category>> categories = new LinkedHashMap<>();
        putCategories(GroupNames.Cancellation, categories);
        putCategories(GroupNames.Medical, categories);
        putCategories(GroupNames.Evacuation, categories);
        putCategories(GroupNames.Loss_or_Delay, categories);
        putCategories(GroupNames.Accidental_Death, categories);
        putCategories(GroupNames.Sports, categories);
        putCategories(GroupNames.Other_Benefits, categories);
        return categories;
    }

    private void putCategories(String groupName, Map<String, List<Category>> categories) {
        categories.put(groupName, categoryRepository.findByGroupName(groupName,
                new Sort(new Sort.Order(Sort.Direction.ASC, "name"))
        ));
    }

    @Transactional
    public Category save(Category category) {
        Category storedCategory = null;
        if (category.getId() != null && (storedCategory = get(category.getId())) != null) {
            storedCategory.setCode(category.getCode());
            storedCategory.setName(category.getName());
            storedCategory.setGroup(category.getGroup());
            storedCategory.setType(category.getType());
            storedCategory.setValueType(category.getValueType());
            storedCategory.setSliderCategoryBoolean(category.isSliderCategoryBoolean());
            storedCategory.setDisplayAsFilter(category.isDisplayAsFilter());
            storedCategory.setFilterOrder(category.getFilterOrder());
            storedCategory.setTemplate(category.getTemplate());
            storedCategory.setCategoryCondition(category.getCategoryCondition());
        }
        storedCategory = (storedCategory == null) ? category : storedCategory;
        if (storedCategory.getFilterOrder() == null) {
            storedCategory.setFilterOrder(this.getMaxFilterOrder() + 1);
        }
        storedCategory = categoryRepository.saveAndFlush(storedCategory);
        cacheService.invalidateCategoriesAndMetas();
        return storedCategory;
    }

    private void fillCanDelete(Category category) {
        String cantDeleteReasons = "";
        //Check if hard coded in CategoryCodes class
        try {
            CategoryCodes.class.getDeclaredField(category.getCode().replace('-', '_').toUpperCase());
            category.setCanDelete(false);
            cantDeleteReasons += "Category is hard coded in CategoryCodes class\n";
        } catch (NoSuchFieldException e) { }
        //Check if it has category content
        CategoryContent categoryContent = categoryContentService.getByCode(category.getCode());
        if(categoryContent != null) {
            category.setCanDelete(false);
            cantDeleteReasons += "This category has category content " + categoryContent.getName() + " [id = " + categoryContent.getId() + "]\n";
        }
        //Check if is contained in system settings
        SystemSettings systemSettings = systemSettingsService.getSystemSettingsByCategoryId(category.getCode());
        if(systemSettings != null) {
            category.setCanDelete(false);
            cantDeleteReasons += "System setting \"" + systemSettings.getName() + "\" [id = " + systemSettings.getId() + "] contains this category\n";
        }
        //Check if it is contained in a policy
        List<PolicyMeta> policies = policyMetaService.getAll();
        int policyReasons = 0;
        for (PolicyMeta policy : policies) {
            for (PolicyMetaCategory policyMetaCategory : policy.getPolicyMetaCategories()) {
                if (policyMetaCategory.getCategory().getCode().equals(category.getCode())) {
                    category.setCanDelete(false);
                    cantDeleteReasons += "Policy \"" + policy.getDisplayName() + "\" [id = " + policy.getId() + ", uniqueCode = " + policy.getUniqueCode() + "]" +
                            " of vendor \"" + policy.getVendor().getName()
                            + "\" contains this category\n";
                    if(++policyReasons > MAX_POLICY_REASONS) {
                        cantDeleteReasons += "...\n";
                        break;
                    }
                }
            }
            if(policyReasons > MAX_POLICY_REASONS) break;
        }
        category.setCantDeleteReason(cantDeleteReasons);
        category.setCanDelete(StringUtils.isBlank(cantDeleteReasons));
    }

    @Transactional
    public void delete(Long categoryId) {
        categoryRepository.delete(categoryId);
    }


    @Transactional(readOnly = true)
    public Category getByFilterOrder(Integer filterOrder) {
        List<Category> categoryList = categoryRepository.findByFilterOrder(filterOrder);
        if (CollectionUtils.isNotEmpty(categoryList)) {
            return categoryList.get(0);
        }
        return null;
    }

    @Transactional
    public void swap(Category first, Category second) {
        int index = first.getFilterOrder();
        first.setFilterOrder(second.getFilterOrder());
        second.setFilterOrder(index);
        categoryRepository.saveAndFlush(first);
        categoryRepository.saveAndFlush(second);
    }

    @Transactional
    public List<Category> reSort() {
        List<Category> categoryList = getAllCategories();

        for (int i = 0; i < categoryList.size(); i++) {
            categoryList.get(i).setFilterOrder(i);
        }
        categoryList.forEach(category -> { categoryRepository.saveAndFlush(category); });
        return categoryList;
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByPlanType(PlanType planType){
        return groupService.getGroupsByPlanType(planType).stream().flatMap(group -> group.getCategoryList().stream()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean hasTemplate(PolicyMetaCategory policyMetaCategory) {
        Category category = get(policyMetaCategory.getCategory().getId());
        Hibernate.initialize(category.getSubcategoriesList());
        return StringUtils.isNotBlank(category.getTemplate()) ||
                CollectionUtils.isNotEmpty(category.getSubcategoriesList()) && category.getSubcategoriesList().stream().anyMatch(subcategory -> StringUtils.isNotBlank(subcategory.getTemplate()));
    }

    public static boolean isLookBackPeriod(Category category) {
        return Objects.equals(category.getCode(), CategoryCodes.LOOK_BACK_PERIOD);
    }
}
