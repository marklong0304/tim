package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.page.Page;
import com.travelinsurancemaster.model.dto.cms.page.PageStatus;
import com.travelinsurancemaster.model.dto.cms.page.PageType;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by Chernov Artur on 23.07.15.
 */

@Repository
public interface PageRepository extends CacheableJpaRepository<Page, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Page> findByNameAndDeletedFalse(String name);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Long countByStatusAndDeletedFalse(PageStatus status);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Page> findByStatusAndDeletedFalse(PageStatus status);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Page> findAllByPageTypeAndDeletedFalse(PageType pageType, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Page> findAllByPageTypeNotAndDeletedFalse(PageType pageType, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Page findByNameAndPageTypeAndDeletedFalse(String name, PageType pageType);
}
