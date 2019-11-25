package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.AffiliateLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Chernov Artur on 25.08.15.
 */

@Repository
public interface AffiliateLinkRepository extends JpaRepository<AffiliateLink, Long> {
    AffiliateLink findByCode(String code);
}
