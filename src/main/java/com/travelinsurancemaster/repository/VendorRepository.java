package com.travelinsurancemaster.repository;

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
public interface VendorRepository extends CacheableJpaRepository<Vendor, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Vendor findByCode(String code);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Vendor findByName(String name);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Vendor findByCodeAndDeletedDateIsNull(String code);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Long countByCode(String code);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Vendor> findByActiveTrueAndDeletedDateIsNull(Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Vendor> findByActiveTrueAndTestFalseAndDeletedDateIsNull(Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Vendor> findAllByDeletedDateIsNull(Sort sort);

    @Query(value="SELECT GREATEST(created, modified) AS tableModifed FROM vendor ORDER BY tableModifed DESC NULLS LAST LIMIT 1", nativeQuery = true)
    Date getTableModified();
}