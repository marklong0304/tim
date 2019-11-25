package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.page.CategoryContent;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by Chernov Artur on 20.10.15.
 */

@Repository
public interface CategoryContentRepository extends CacheableJpaRepository<CategoryContent, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CategoryContent> findAllByDeletedFalse(Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CategoryContent> findByCodeAndDeletedFalse(String code);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CategoryContent> findBySortOrderAndDeletedFalse(Integer sortIndex);
}
