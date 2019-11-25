package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 4/10/15.
 */
public interface PolicyMetaCategoryRepository extends CacheableJpaRepository<PolicyMetaCategory, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    PolicyMetaCategory findByPolicyMetaUniqueCodeAndCategoryCode(String policyUniqueCode, String categoryCode);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaCategory> findByPolicyMetaUniqueCode(String policyUniqueCode);

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_category ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}