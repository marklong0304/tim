package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.menu.Menu;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;

/**
 * Created by Chernov Artur on 23.07.15.
 */

@Repository
public interface MenuRepository extends CacheableJpaRepository<Menu, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Menu findByTitle(String title);
}
