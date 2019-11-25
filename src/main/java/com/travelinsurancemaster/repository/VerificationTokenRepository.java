package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.security.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Chernov Artur on 12.05.15.
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
