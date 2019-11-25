package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by Chernov Artur on 20.10.15.
 */

@Repository
public interface PolicyMetaCategoryContentRepository extends CacheableJpaRepository<PolicyMetaCategoryContent, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaCategoryContent> findAllByDeletedFalse();

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    PolicyMetaCategoryContent findByCategoryIdAndPolicyMetaPageForCategoryIdAndDeletedFalse(Long categorytId, Long policyMetaPageId);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaCategoryContent> findAllByNameAndDeletedFalseAndPolicyMetaPageForCustomCategoryId(String name, Long policyMetaPageId);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaCategoryContent> findAllByNameAndDeletedFalseAndPolicyMetaPageForPlanInfoId(String name, Long policyMetaPageId);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMetaCategoryContent> findAllByNameAndDeletedFalseAndPolicyMetaPageForRestrictionsId(String name, Long policyMetaPageId);

    @QueryHints({@QueryHint(name = "org.hibernate.casheable", value = "true")})
    PolicyMetaCategoryContent findByPackageOptionsIdAndDeletedFalse(Long PackageOptions);

    @QueryHints({@QueryHint(name = "org.hibernate.casheable", value = "true")})
    List<PolicyMetaCategoryContent> findAllByNameAndDeletedFalseAndPolicyMetaPackageId(String name, Long policyMetaPageId);

    @QueryHints({@QueryHint(name = "org.hibernate.casheable", value = "true")})
    List<PolicyMetaCategoryContent> findAllByPolicyMetaPageForCategoryAndDeletedFalse(PolicyMetaPage policyMetaPage);

    @QueryHints({@QueryHint(name = "org.hibernate.casheable", value = "true")})
    List<PolicyMetaCategoryContent> findByPolicyMetaPageForCategoryAndNameAndDeletedFalse(PolicyMetaPage policyMetaPage, String name);

    @QueryHints({@QueryHint(name = "org.hibernate.casheable", value = "true")})
    List<PolicyMetaCategoryContent> findByPolicyMetaPageForPlanInfoAndNameAndDeletedFalse(PolicyMetaPage policyMetaPage, String name);

    @QueryHints({@QueryHint(name = "org.hibernate.casheable", value = "true")})
    List<PolicyMetaCategoryContent> findByPolicyMetaPageForCustomCategoryAndNameAndDeletedFalse(PolicyMetaPage policyMetaPage, String name);

    @QueryHints({@QueryHint(name = "org.hibernate.casheable", value = "true")})
    List<PolicyMetaCategoryContent> findByPolicyMetaPageForRestrictionsAndNameAndDeletedFalse(PolicyMetaPage policyMetaPage, String name);

    @Query(value="SELECT GREATEST(create_date, modified_date) AS tableModifed FROM policy_meta_category_content ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}
