package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.SubcategoryValue;
import com.travelinsurancemaster.repository.SubcategoryValueRepository;
import com.travelinsurancemaster.services.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ritchie on 7/8/16.
 */
@Service
@Transactional
public class SubcategoryValueService {

    @Lazy
    @Autowired
    private CacheService cacheService;

    @Autowired
    private SubcategoryValueRepository subcategoryValueRepository;

    public SubcategoryValue save(SubcategoryValue subcategoryValue) {
        SubcategoryValue storedSubcategoryValue = null;
        if (subcategoryValue.getId() != null && (storedSubcategoryValue = getSubcategoryValueById(subcategoryValue.getId())) != null) {
            storedSubcategoryValue.setSubcategoryValue(subcategoryValue.getSubcategoryValue());
            storedSubcategoryValue.setSubcategory(subcategoryValue.getSubcategory());
            storedSubcategoryValue.setPolicyMetaCategoryValue(subcategoryValue.getPolicyMetaCategoryValue());
        }
        storedSubcategoryValue = (storedSubcategoryValue == null) ? subcategoryValue : storedSubcategoryValue;
        cacheService.invalidateCategoriesAndMetas();
        return subcategoryValueRepository.saveAndFlush(storedSubcategoryValue);
    }

    /**
     * @return subcategory - name-value pair, applied for category velocity template.
     */
    @Transactional(readOnly = true)
    public SubcategoryValue getSubcategoryValueById(Long id) {
        return subcategoryValueRepository.findOne(id);
    }
}
