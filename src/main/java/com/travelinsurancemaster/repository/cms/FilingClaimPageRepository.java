package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.page.FilingClaimPage;
import com.travelinsurancemaster.model.dto.cms.page.PageStatus;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by Chernov Artur on 04.12.15.
 */

@Repository
public interface FilingClaimPageRepository extends CacheableJpaRepository<FilingClaimPage, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<FilingClaimPage> findByNameAndDeletedFalse(String name);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Long countByStatusAndDeletedFalse(PageStatus status);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<FilingClaimPage> findByVendorPageAndDeletedFalse(VendorPage vendorPage);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<FilingClaimPage> findByStatusAndDeletedFalse(PageStatus status);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<FilingClaimPage> findAllByStatusAndDeletedFalse(PageStatus status, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<FilingClaimPage> findAllByDeletedFalse(Sort sort);
}
