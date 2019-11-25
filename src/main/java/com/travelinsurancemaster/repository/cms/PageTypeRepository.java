package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.page.PageType;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;

/**
 * Created by Chernov Artur on 23.07.15.
 */

@Repository
public interface PageTypeRepository extends CacheableJpaRepository<PageType, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    PageType findByName(String name);

}
