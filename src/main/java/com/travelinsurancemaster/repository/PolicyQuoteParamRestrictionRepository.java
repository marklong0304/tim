package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyQuoteParamRestriction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by N.Kurennoy on 19.05.2016.
 */
public interface PolicyQuoteParamRestrictionRepository extends CacheableJpaRepository<PolicyQuoteParamRestriction, Long>{

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyQuoteParamRestriction> findByPolicyQuoteParamId(Long id);

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_quote_param_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}