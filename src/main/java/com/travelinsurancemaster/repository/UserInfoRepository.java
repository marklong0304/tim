package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ritchie on 4/21/15.
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
}
