package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.Subcategory;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ritchie on 7/6/16.
 */
public interface SubcategoryRepository extends CacheableJpaRepository<Subcategory, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Subcategory findBySubcategoryCodeAndCategoryId(String subcategoryCode, Long categoryId);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Subcategory> findByCategoryIdOrderByOrderAsc(Long categoryId);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Subcategory> findByCategoryIdAndOrder(Long categoryId, Integer order);
}
