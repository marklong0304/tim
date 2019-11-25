package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMetaCategoryRestriction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 3/31/16.
 */
public interface PolicyMetaCategoryRestrictionRepository extends CacheableJpaRepository<PolicyMetaCategoryRestriction, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaCategoryRestriction> findByPolicyMetaCategoryId(Long policyMetaCategoryId);

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_category_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}