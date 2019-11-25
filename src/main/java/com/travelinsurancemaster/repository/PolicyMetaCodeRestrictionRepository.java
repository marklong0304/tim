package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMetaCodeRestriction;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by ritchie on 4/4/16.
 */
public interface PolicyMetaCodeRestrictionRepository extends CacheableJpaRepository<PolicyMetaCodeRestriction, Long> {

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_code_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}