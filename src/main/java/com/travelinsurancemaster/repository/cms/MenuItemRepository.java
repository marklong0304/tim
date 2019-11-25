package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.menu.MenuItem;
import com.travelinsurancemaster.model.dto.cms.page.Page;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by Chernov Artur on 23.07.15.
 */

@Repository
public interface MenuItemRepository extends CacheableJpaRepository<MenuItem, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<MenuItem> findByPage(Page page);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<MenuItem> findByMenuTitle(String menuTitle);
}
