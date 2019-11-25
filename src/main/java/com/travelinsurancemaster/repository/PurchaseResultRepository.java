package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.purchase.PurchaseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by raman on 15.08.19.
 */

@Repository
public interface PurchaseResultRepository extends JpaRepository<PurchaseResult, Long>, JpaSpecificationExecutor<PurchaseResult> {
}
