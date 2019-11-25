package com.travelinsurancemaster.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.QueryHint;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Alex on 14.09.2015.
 */
@NoRepositoryBean
public interface CacheableJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Override
    Page<T> findAll(Pageable pageable);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Override
    List<T> findAll();
    
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Override
    List<T> findAll(Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Override
    List<T> findAll(Iterable<ID> ids);
}
