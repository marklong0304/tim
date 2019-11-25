package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Chernov Artur on 07.05.15.
 */

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>, JpaSpecificationExecutor<Purchase> {

    @Query("Select p from Purchase p where p.user = ?1 and p.purchaseQuoteRequest.returnDate >= CURRENT_DATE and p.success = true and p.cancelled is null and p.purchaseType = ?2")
    List<Purchase> findCurrentPolicies(User user, Purchase.PurchaseType type);

    Purchase findByPurchaseUuid(String purchaseUuid);

    List<Purchase> findByPolicyNumberAndPolicyMetaVendorCode(String policyNumber, String code);

    List<Purchase> findByPolicyNumberAndPolicyMetaVendorName(String policyNumber, String name);

    @Query("Select p from Purchase p where p.success = true and p.cancelled is null and p.purchaseQuoteRequest.returnDate = ?1 and p.purchaseType = ?2")
    List<Purchase> findEndTripPolicies(LocalDate returnDate, Purchase.PurchaseType purchaseType);

    List<Purchase> findAllByUserId(Long id);
}
