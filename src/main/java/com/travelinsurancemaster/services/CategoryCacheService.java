package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.dto.Category;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service

@Transactional(propagation = Propagation.SUPPORTS)
public class CategoryCacheService {

    private final CategoryService categoryService;

    @Cacheable(value = CacheConfig.CATEGORIES_CACHE, key = "#root.methodName")
    public Map<String, Category> getCategoryMap() {
        List<Category> categories = categoryService.getAllCategories();
        Map<String, Category> categoryMap = new HashMap<>();
        categories.forEach(category -> categoryMap.put(category.getCode(), category));
        return categoryMap;
    }

    public Category getCategory(Long id) {
        return getCategoryMap().values().stream()
                .filter(category -> Objects.equals(id, category.getId()))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + "not found"));
    }

    public Category getCategory(String code) {
        return getCategoryMap().get(code);
    }

    public CategoryCacheService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
}
