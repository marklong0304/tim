package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.Group;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;

/**
 * Created by ritchie on 4/9/15.
 */
public interface GroupRepository extends CacheableJpaRepository<Group, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Group findByName(String name);
}
