package com.travelinsurancemaster.repository.system;

import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;

/**
 * Created by Chernov Artur on 25.08.15.
 */

@Repository
public interface SystemSettingsRepository extends CacheableJpaRepository<SystemSettings, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    SystemSettings findByName(String name);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Query("select systemSettings from SystemSettings systemSettings " +
            "where systemSettings.planDescriptionCategory1.code = ?1 " +
            "or systemSettings.planDescriptionCategory2.code = ?1 " +
            "or systemSettings.planDescriptionCategory3.code = ?1 " +
            "or systemSettings.planDescriptionCategory4.code = ?1 " +
            "or systemSettings.planDescriptionCategory5.code = ?1 " +
            "or systemSettings.planDescriptionCategory6.code = ?1")
    SystemSettings findByCategoryId(String categoryCode);
}
