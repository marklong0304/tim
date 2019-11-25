package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 4/4/16.
 */
public interface PolicyMetaCodeRepository extends CacheableJpaRepository<PolicyMetaCode, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    PolicyMetaCode findFirstByPolicyMetaId(Long policyMetaId);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaCode> findByCode(String code);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaCode> findByPolicyMetaUniqueCode(String policyUniqueCode);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaCode> findByCodeAndPolicyMetaVendorCode(String policyMetaCode, String vendorCode);

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_code ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}