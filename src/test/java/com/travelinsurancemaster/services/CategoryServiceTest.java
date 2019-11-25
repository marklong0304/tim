package com.travelinsurancemaster.services;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.Group;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

;
;

/**
 * Created by arthurchernov on 03.06.17.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryCacheService categoryCacheService;

    @Test
    public void getAllCategories() throws Exception {
        Assert.assertEquals(31, categoryService.getAllCategories().size());
    }

    @Test
    public void getAllCategoriesWithoutConditional() throws Exception {
        Assert.assertEquals(categoryService.getAllCategoriesWithoutConditional().size(), 30);
    }

    @Test
    public void getAllDisplayedAsFilter() throws Exception {
        Assert.assertEquals(categoryService.getAllDisplayedAsFilter().size(), 30);
    }

    @Test
    public void getAllFillCanDelete() throws Exception {
        Supplier<Stream<Category>> categories = () -> categoryService.getAllFillCanDelete().stream();
        Assert.assertTrue(categories.get().filter(category -> category.getId() == 31).findFirst().get().isCanDelete());
        Assert.assertFalse(categories.get().filter(category -> category.getId() == 1).findFirst().get().isCanDelete());
    }

    @Test
    public void getMaxFilterOrder() throws Exception {
        Assert.assertEquals(categoryService.getMaxFilterOrder().intValue(), 30);
    }

    @Test
    public void getByCode() throws Exception {
        Assert.assertEquals(categoryService.getByCode(CategoryCodes.TRIP_INTERRUPTION).getCode(), CategoryCodes.TRIP_INTERRUPTION);
    }

    @Test
    public void get() throws Exception {
        Assert.assertNull(categoryService.get(999L));
        Assert.assertEquals(categoryService.get(1L).getCode(), CategoryCodes.TRIP_CANCELLATION);
    }

    @Test
    public void getFillSliderAndCanDelete() throws Exception {
        Assert.assertTrue(categoryService.getFillCanDelete(31L).isCanDelete());
        Assert.assertFalse(categoryService.getFillCanDelete(1L).isCanDelete());
    }

    @Test
    public void getByCategoryType() throws Exception {
        Assert.assertEquals(categoryService.getByCategoryType(Category.CategoryType.CONDITIONAL).size(), 1);
        Assert.assertTrue(categoryService.getByCategoryType(Category.CategoryType.CATALOG).stream().anyMatch(category -> category.getId() == 11));
    }

    @Test
    public void getGroupCategoryMap() throws Exception {
        Assert.assertEquals(categoryService.getGroupCategoryMap().size(), 7);
    }

    @Test
    public void save() throws Exception {
        Category testCategory = getTestCategory();
        Category saved = categoryService.save(testCategory);
        Category found = categoryService.get(saved.getId());
        Assert.assertEquals(categoryService.getAllCategories().size(), 32);
        Assert.assertEquals(found.getCode(), testCategory.getCode());
        categoryService.delete(found.getId());
        Assert.assertNull(categoryService.get(found.getId()));
    }

    @Test
    public void updateCategory() throws Exception {
        Category category = categoryService.get(1L);
        Assert.assertEquals(category.isDisplayAsFilter(), true);
        category.setDisplayAsFilter(false);
        categoryService.save(category);
        category = categoryService.get(1L);
        Assert.assertEquals(category.isDisplayAsFilter(), false);
        category.setDisplayAsFilter(true);
        categoryService.save(category);
        Assert.assertEquals(category.isDisplayAsFilter(), true);
    }

    @Test
    public void delete() throws Exception {
        Assert.assertEquals(categoryService.getAllCategories().size(), 31);
        Category testCategory = getTestCategory();
        Category saved = categoryService.save(testCategory);
        Assert.assertEquals(categoryService.getAllCategories().size(), 32);
        categoryService.delete(saved.getId());
        Assert.assertEquals(categoryService.getAllCategories().size(), 31);
    }

    @Test
    public void getCategoryMap() throws Exception {
        Assert.assertEquals(categoryCacheService.getCategoryMap().size(), categoryService.getAllCategories().size());
    }

    @Test
    public void getByFilterOrder() throws Exception {
        Assert.assertEquals(categoryService.getByFilterOrder(15).getFilterOrder().intValue(), 15);
    }

    @Test
    public void swap() throws Exception {
        Category first = categoryService.get(1L);
        Category second = categoryService.get(2L);
        categoryService.swap(first, second);
        first = categoryService.get(1L);
        second = categoryService.get(2L);
        Assert.assertEquals(first.getFilterOrder().intValue(), 1);
        Assert.assertEquals(second.getFilterOrder().intValue(), 0);
        categoryService.swap(first, second);
        first = categoryService.get(1L);
        second = categoryService.get(2L);
        Assert.assertEquals(first.getFilterOrder().intValue(), 0);
        Assert.assertEquals(second.getFilterOrder().intValue(), 1);
    }

    @Test
    public void reSort() throws Exception {
        Category first = categoryService.get(1L);
        first.setFilterOrder(-1);
        categoryService.save(first);
        Assert.assertEquals(first.getFilterOrder().intValue(), -1);
        categoryService.reSort();
        first = categoryService.get(1L);
        Assert.assertEquals(first.getFilterOrder().intValue(), 0);
    }

    @Test
    public void getCategoriesByPlanType() throws Exception {
        Assert.assertTrue(categoryService.getCategoriesByPlanType(PlanType.COMPREHENSIVE).stream().anyMatch(category -> Objects.equals(category.getCode(), CategoryCodes.TRIP_CANCELLATION)));
        Assert.assertFalse(categoryService.getCategoriesByPlanType(PlanType.LIMITED).stream().anyMatch(category -> Objects.equals(category.getCode(), CategoryCodes.TRIP_CANCELLATION)));
    }

    @Test
    public void isLookBackPeriod() throws Exception {
        Assert.assertFalse(CategoryService.isLookBackPeriod(getTestCategoryByCode(CategoryCodes.TRIP_CANCELLATION)));
        Assert.assertTrue(CategoryService.isLookBackPeriod(getTestCategoryByCode(CategoryCodes.LOOK_BACK_PERIOD)));
    }

    private Category getTestCategoryByCode(String code) {
        Category category = new Category();
        category.setCode(code);
        return category;
    }

    private Category getTestCategory() {
        Category category = new Category();
        category.setName("Test category save");
        category.setCode("test-category-save");
        category.setType(Category.CategoryType.SIMPLE);
        category.setDisplayAsFilter(true);
        category.setGroup(getTestGroup());
        return category;
    }

    private Group getTestGroup() {
        Group group = new Group();
        group.setId(1L);
        return group;
    }

}