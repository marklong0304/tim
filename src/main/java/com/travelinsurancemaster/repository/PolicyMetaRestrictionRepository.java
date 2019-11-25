package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMetaRestriction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 4/9/15.
 */
public interface PolicyMetaRestrictionRepository extends CacheableJpaRepository<PolicyMetaRestriction, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaRestriction> findByPolicyMetaId(Long id);

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}
