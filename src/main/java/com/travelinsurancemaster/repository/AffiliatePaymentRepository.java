package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.AffiliatePayment;
import com.travelinsurancemaster.model.dto.query.IAffiliateCompanyBalance;
import com.travelinsurancemaster.model.dto.query.IAffiliateUserBalance;
import com.travelinsurancemaster.model.dto.query.IAffiliatePaymentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Raman on 24.06.19.
 */

public interface AffiliatePaymentRepository extends JpaRepository<AffiliatePayment, Long>, JpaSpecificationExecutor<AffiliatePayment> {

    @Query(value="SELECT sb.affiliate_user_id AS affiliateUserId, sb.balance"
            + " FROM ("
            + " SELECT b.affiliate_user_id, SUM(b.balance) AS balance"
            + " FROM ("
            + " SELECT ap.affiliate_user_id, SUM(CASE WHEN p.cancelled IS NOT NULL THEN 0 ELSE ac.salary_to_pay END - ac.salary_paid) AS balance"
            + " FROM affiliate_commission ac"
            + " LEFT JOIN purchase p ON p.affiliate_commission_id = ac.id"
            + " LEFT JOIN affiliate_payment ap ON ap.id = ac.affiliate_payment_id"
            + " WHERE ap.affiliate_user_id IS NOT null"
            + " GROUP BY ap.affiliate_user_id"
            + " UNION"
            + " SELECT ap.affiliate_user_id, - SUM(sc.salary_paid) AS balance"
            + " FROM salary_correction sc"
            + " LEFT JOIN affiliate_payment ap ON ap.id = sc.affiliate_payment_id"
            + " WHERE ap.affiliate_user_id IS NOT NULL"
            + " GROUP BY ap.affiliate_user_id"
            + " ) b"
            + " GROUP BY b.affiliate_user_id"
            + " ) sb"
            + " WHERE sb.balance <> 0"
            , nativeQuery = true)
    List<IAffiliateUserBalance> getAffiliateUserBalances();

    @Query(value="SELECT sb.affiliate_company_id AS affiliateCompanyId, sb.balance"
            + " FROM ("
            + " SELECT b.affiliate_company_id, SUM(b.balance) AS balance"
            + " FROM ("
            + " SELECT ap.affiliate_company_id, SUM(CASE WHEN p.cancelled IS NOT NULL THEN 0 ELSE ac.salary_to_pay END - ac.salary_paid) AS balance"
            + " FROM affiliate_commission ac"
            + " LEFT JOIN purchase p ON p.affiliate_commission_id = ac.id"
            + " LEFT JOIN affiliate_payment ap ON ap.id = ac.affiliate_payment_id"
            + " WHERE ap.affiliate_company_id IS NOT null"
            + " GROUP BY ap.affiliate_company_id"
            + " UNION"
            + " SELECT ap.affiliate_company_id, - SUM(sc.salary_paid) AS balance"
            + " FROM salary_correction sc"
            + " LEFT JOIN affiliate_payment ap ON ap.id = sc.affiliate_payment_id"
            + " WHERE ap.affiliate_company_id IS NOT NULL"
            + " GROUP BY ap.affiliate_company_id"
            + " ) b"
            + " GROUP BY b.affiliate_company_id"
            + " ) sb"
            + " WHERE sb.balance <> 0"
            , nativeQuery = true)
    List<IAffiliateCompanyBalance> getAffiliateCompanyBalances();
    
    @Query(value="SELECT cp.* FROM ("
            + "SELECT p.purchase_uuid AS purchaseUuid, p.purchase_date AS purchaseDate, p.cancelled IS NOT NULL AS cancelled"
            + ", v.name AS vendorName, p.policy_number AS policyNumber, pm.display_name AS displayName"
            + ", ac.salary_to_pay AS salaryToPay, ac.salary_paid AS salaryPaid, p.note, ap.payment_date AS paymentDate, ap.id AS paymentId"
            + " FROM affiliate_commission ac"
            + " LEFT JOIN purchase p ON p.affiliate_commission_id = ac.id"
            + " LEFT JOIN affiliate_payment ap ON ap.id = ac.affiliate_payment_id"
            + " LEFT JOIN policy_meta pm ON pm.id = p.policy_meta_id"
            + " LEFT JOIN vendor v ON v.id = pm.vendor_id"
            + " WHERE ap.affiliate_user_id = ?1 AND ac.salary_to_pay <> ac.salary_paid"
            + " UNION"
            + " SELECT NULL AS purchaseUuid, NULL AS purchaseDate, false AS cancelled"
            + ", NULL AS vendorName, NULL AS policyNumber, NULL AS displayName"
            + ", NULL AS salaryToPay, sc.salary_paid AS salaryPaid, sc.note, ap.payment_date AS paymentDate, ap.id AS paymentId"
            + " FROM salary_correction sc"
            + " LEFT JOIN affiliate_payment ap ON ap.id = sc.affiliate_payment_id"
            + " WHERE ap.affiliate_user_id = ?1"
            + " ) cp"
            + " ORDER BY cp.paymentDate DESC"
            , nativeQuery = true)
    List<IAffiliatePaymentData> getAffiliateUserPayments(Long affiliateUserId);

    @Query(value="SELECT cp.* FROM ("
            + "SELECT p.purchase_uuid AS purchaseUuid, p.purchase_date AS purchaseDate, p.cancelled IS NOT NULL AS cancelled"
            + ", v.name AS vendorName, p.policy_number AS policyNumber, pm.display_name AS displayName"
            + ", ac.salary_to_pay AS salaryToPay, ac.salary_paid AS salaryPaid, p.note, ap.payment_date AS paymentDate, ap.id AS paymentId"
            + " FROM affiliate_commission ac"
            + " LEFT JOIN purchase p ON p.affiliate_commission_id = ac.id"
            + " LEFT JOIN affiliate_payment ap ON ap.id = ac.affiliate_payment_id"
            + " LEFT JOIN policy_meta pm ON pm.id = p.policy_meta_id"
            + " LEFT JOIN vendor v ON v.id = pm.vendor_id"
            + " WHERE ap.affiliate_company_id = ?1 AND ac.salary_to_pay <> ac.salary_paid"
            + " UNION"
            + " SELECT NULL AS purchaseUuid, NULL AS purchaseDate, false AS cancelled"
            + ", NULL AS vendorName, NULL AS policyNumber, NULL AS displayName"
            + ", NULL AS salaryToPay, sc.salary_paid AS salaryPaid, sc.note, ap.payment_date AS paymentDate, ap.id AS paymentId"
            + " FROM salary_correction sc"
            + " LEFT JOIN affiliate_payment ap ON ap.id = sc.affiliate_payment_id"
            + " WHERE ap.affiliate_company_id = ?1"
            + " ) cp"
            + " ORDER BY cp.paymentDate DESC"
            , nativeQuery = true)
    List<IAffiliatePaymentData> getAffiliateCompanyPayments(Long affiliateCompanyId);
}