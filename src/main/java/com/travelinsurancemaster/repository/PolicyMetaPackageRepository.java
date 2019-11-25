package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaPackage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander.Isaenco
 */
public interface PolicyMetaPackageRepository extends CacheableJpaRepository<PolicyMetaPackage, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPackage> findByPolicyMeta(PolicyMeta policyMeta);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaPackage> findAllByCodeIn(List<String> codes);

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_package ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}