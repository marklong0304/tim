package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.dto.Subcategory;
import com.travelinsurancemaster.repository.SubcategoryRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Created by ritchie on 7/6/16.
 */

@Service
public class SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;

    public SubcategoryService(SubcategoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

    @Transactional(readOnly = true)
    public Subcategory getSubcategory(String subcategoryCode, Long categoryId) {
        return subcategoryRepository.findBySubcategoryCodeAndCategoryId(subcategoryCode, categoryId);
    }

    @Transactional
    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public Subcategory save(Subcategory subcategory) {
        Subcategory storedSubcategory = null;
        if (subcategory.getId() != null && (storedSubcategory = getSubcategoryById(subcategory.getId())) != null) {
            storedSubcategory.setSubcategoryName(subcategory.getSubcategoryName());
            storedSubcategory.setSubcategoryCode(subcategory.getSubcategoryCode());
            storedSubcategory.setTemplate(subcategory.getTemplate());
            storedSubcategory.setCategory(subcategory.getCategory());
        }
        if (storedSubcategory == null){
            storedSubcategory = subcategory;
            List<Subcategory> subcategories = getSubcategories(subcategory.getCategory().getId());
            storedSubcategory.setOrder(subcategories.size()+1);
        };
        return subcategoryRepository.saveAndFlush(storedSubcategory);
    }

    @Transactional(readOnly = true)
    public List<Subcategory> getSubcategories(Long categoryId) {
        return subcategoryRepository.findByCategoryIdOrderByOrderAsc(categoryId);
    }

    @Transactional(readOnly = true)
    public Subcategory getSubcategoryById(Long id) {
        return subcategoryRepository.findOne(id);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void delete(Subcategory subcategory) {
        subcategoryRepository.delete(subcategory);
        reSort(subcategory.getCategory().getId());
    }

    @Transactional(readOnly = true)
    public Subcategory getByCategoryIdAndSortOrder(Long categoryId, Integer order) {
        List<Subcategory> subcategories = subcategoryRepository.findByCategoryIdAndOrder(categoryId, order);
        if (CollectionUtils.isNotEmpty(subcategories)) {
            return subcategories.get(0);
        }
        return null;
    }

    private void swap(Subcategory first, Subcategory second) {
        int index = first.getOrder();
        first.setOrder(second.getOrder());
        second.setOrder(index);
        subcategoryRepository.saveAndFlush(first);
        subcategoryRepository.saveAndFlush(second);
    }

    @Transactional
    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void up(Subcategory subcategory) {
        Optional<Subcategory> prevSubcategory = Optional.ofNullable(getByCategoryIdAndSortOrder(subcategory.getCategory().getId(), subcategory.getOrder() - 1));
        if (!prevSubcategory.isPresent()) {
            throw new NullPointerException("Subcategory not found");
        }
        swap(prevSubcategory.get(), subcategory);
    }

    @Transactional
    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void down(Subcategory subcategory) {
        Optional<Subcategory> nextSubcategory = Optional.ofNullable(getByCategoryIdAndSortOrder(subcategory.getCategory().getId(), subcategory.getOrder() + 1));
        if (!nextSubcategory.isPresent()) {
            throw new NullPointerException("Subcategory not found");
        }
        swap(nextSubcategory.get(), subcategory);
    }

    private void reSort(Long categoryId) {
        List<Subcategory> subcategories = getSubcategories(categoryId);
        IntStream.range(0, subcategories.size()).forEach(idx -> subcategories.get(idx).setOrder(idx+1));
        subcategories.forEach(subcategory -> { subcategoryRepository.saveAndFlush(subcategory); });
    }
}
