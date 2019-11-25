package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMetaPackageRestriction;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * @author Alexander.Isaenco
 */
public interface PolicyMetaPackageRestrictionRepository extends CacheableJpaRepository<PolicyMetaPackageRestriction, Long> {

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_package_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}