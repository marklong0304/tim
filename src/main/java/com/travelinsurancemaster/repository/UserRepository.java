package com.travelinsurancemaster.repository;

/**
 * Created by Chernov Artur on 17.04.15.
 */

import com.travelinsurancemaster.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByNameAndDeletedNull(String name);

    User findById(Long id);

    User findByIdAndDeletedNull(Long id);

    User findByEmailIgnoreCaseAndDeletedNull(String email);

    User findByEmailIgnoreCase(String email);

    List<User> findByDeletedNull();

    List<User> findAllByDeletedNull();

    List<User> findAllByDeletedNullOrderByEmail();

    List<User> findByIdInOrderByEmail(List<Long> ids);

    List<User> findByDeletedNullAndAffiliationRequestedNotNullAndAffiliationApprovedNullOrderByAffiliationRequestedDesc();

    List<User> findByDeletedNullAndAffiliationApprovedNotNull();
}