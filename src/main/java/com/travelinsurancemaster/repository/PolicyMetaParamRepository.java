package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMetaCategory;

/**
 * Created by ritchie on 4/9/15.
 */
public interface PolicyMetaParamRepository extends CacheableJpaRepository<PolicyMetaCategory, Long> {
}
