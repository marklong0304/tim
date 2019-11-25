package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.cms.page.FilingClaimContact;
import com.travelinsurancemaster.model.dto.cms.page.PageStatus;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by Chernov Artur on 18.10.15.
 */

@Repository
public interface PolicyMetaPageRepository extends CacheableJpaRepository<PolicyMetaPage, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPage> findByNameAndDeletedFalse(String name);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPage> findByStatusAndDeletedFalse(PageStatus status, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Long countByStatusAndDeletedFalse(PageStatus status);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPage> findByVendorPageVendorAndDeletedFalse(Vendor vendor, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPage> findByVendorPageAndDeletedFalse(VendorPage vendorPage);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPage> findByVendorPageAndDeletedFalse(VendorPage vendorPage, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPage> findByVendorPageAndDeletedFalseAndFilingClaimContact(VendorPage vendorPage, FilingClaimContact contact, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPage> findByVendorPage(VendorPage vendorPage, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPage> findByVendorPageAndStatusAndDeletedFalse(VendorPage vendorPage, PageStatus status, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    PolicyMetaPage findByIdAndCertificateNotNull(Long id);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Long countByVendorPageAndDeletedFalse(VendorPage vendorPage);

    @Query(value="SELECT GREATEST(create_date, modified_date) AS tableModifed FROM policy_meta_page ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}