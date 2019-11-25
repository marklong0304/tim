package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 4/9/15.
 */
public interface CategoryRepository extends CacheableJpaRepository<Category, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Category findByCode(String code);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Category> findByType(Category.CategoryType type);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Category> findByGroupName(String groupName, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Category> findByFilterOrder(Integer filterOrder);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Category findFirstByOrderByFilterOrderDesc();

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Category> findByTypeNotLike(Category.CategoryType categoryType, Sort filterOrder);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Category> findAllByCodeIn(List<String> code);

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM category ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}