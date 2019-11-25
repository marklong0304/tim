package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.cms.page.PageStatus;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by Chernov Artur on 15.10.15.
 */

@Repository
public interface VendorPageRepository extends CacheableJpaRepository<VendorPage, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<VendorPage> findByNameAndDeletedFalse(String name);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Long countByStatusAndDeletedFalse(PageStatus status);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<VendorPage> findByVendorAndDeletedFalse(Vendor vendor);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<VendorPage> findByVendorAndStatusAndDeletedFalse(Vendor vendor, PageStatus status);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<VendorPage> findByStatusAndDeletedFalse(PageStatus status);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<VendorPage> findAllByStatusAndDeletedFalse(PageStatus status, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<VendorPage> findAllByDeletedFalseAndDeletedDateIsNull(Sort sort);
}
