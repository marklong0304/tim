package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by Chernov Artur on 15.12.15.
 */
public interface CertificateRepository extends CacheableJpaRepository<Certificate, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Certificate> findByVendorCodeAndPolicyMetaCodeAndDefaultPolicyTrueAndDeletedFalse(String vendorCode, String policyMetaCode);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Certificate findByUuidAndDeletedFalse(String uuid);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Certificate findByIdAndDeletedFalse(Long id);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Certificate> findByVendorCodeAndDeletedFalse(String vendorCode, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Certificate> findByVendorCodeAndPolicyMetaCodeAndDeletedFalse(String vendorCode, String policyMetaCode, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Certificate> findByVendorCodeAndPolicyMetaCodeAndDefaultPolicyAndDeletedFalse(String vendorCode, String policyMetaCode, boolean defaultPolicy, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Certificate> findByDeletedFalse();
}
