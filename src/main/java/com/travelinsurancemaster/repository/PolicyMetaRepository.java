package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 4/9/15.
 */
public interface PolicyMetaRepository extends CacheableJpaRepository<PolicyMeta, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    PolicyMeta findByUniqueCodeAndDeletedDateIsNull(String uniqueCode);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMeta> findByVendorCodeAndVendorActiveTrueAndActiveTrueAndDeletedDateIsNull(String vendorCode);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMeta> findByVendorAndDeletedDateIsNull(Vendor vendor, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PolicyMeta> findByVendorActiveTrueAndActiveTrueAndDeletedDateIsNull();

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();

    @Query(value= "SELECT t.tableModifed FROM ("
            + "(SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(create_date, modified_date) AS tableModifed FROM policy_meta_page ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM subcategory_value ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_category ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_category_value ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_category_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(create_date, modified_date) AS tableModifed FROM policy_meta_category_content ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_package ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_package_value ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_package_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_quote_param ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_quote_param_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_code ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM policy_meta_code_restriction ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM vendor ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + " UNION (SELECT GREATEST(created, modified) AS tableModifed FROM category ORDER BY tableModifed DESC NULLS LAST LIMIT 1)"
            + ") t ORDER BY t.tableModifed DESC NULLS LAST LIMIT 1"
            , nativeQuery = true)
    Date getAnyTableModified();
}