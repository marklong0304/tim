package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;


/**
 * Created by ritchie on 7/8/16.
 */
public interface PolicyMetaCategoryValueRepository extends CacheableJpaRepository<PolicyMetaCategoryValue, Long> {

   @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_category_value ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
   Date getTableModified();
}