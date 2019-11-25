package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.SubcategoryValue;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by ritchie on 7/8/16.
 */
public interface SubcategoryValueRepository extends CacheableJpaRepository<SubcategoryValue, Long> {

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM subcategory_value ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}