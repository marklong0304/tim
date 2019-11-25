package com.travelinsurancemaster.model.dto.json.datatable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.SalaryCorrection;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.purchase.PurchaseQuoteRequest;
import com.travelinsurancemaster.model.dto.purchase.PurchaseTraveler;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.util.DateUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@JsonIgnoreProperties(
    value = {
      "affiliateEmail",
      "userId",
      "userEmail",
      "travelerfirstName",
      "travelerlastName",
      "vendorId",
      "policyId"
    })
@Data
public abstract class DataTableJson {
  private String affiliate;
  private Long affiliateId;
  private String affiliateEmail;
  private String purchaseDate;
  private Long userId;
  private String traveler;
  private String userName;
  private String userEmail;
  private String travelerfirstName;
  private String travelerlastName;
  private String vendor;
  private Long vendorId;
  private String policy;
  private Long policyId;
  private String policyNumber;
  private String totalPrice;
  private String note;
  private String id;

  public DataTableJson(String id, Long affiliateId, String affiliateName) {
    this.id = id;
    this.affiliateId = affiliateId;
    this.affiliate = affiliateName;
    this.purchaseDate = DateUtil.getLocalDateStr(LocalDate.now());
  }

  public DataTableJson(Purchase purchase) {
    PurchaseQuoteRequest purchaseQuoteRequest = purchase.getPurchaseQuoteRequest();
    PurchaseTraveler primaryTraveler = purchaseQuoteRequest.getPrimaryTraveler();
    PolicyMeta policyMeta = purchase.getPolicyMeta();

    User user = purchase.getUser();
    String affiliateName = "";
    String affiliateEmail = "";
    Long affiliateId = -1L;

    if (purchase.getAffiliateCommission() != null) {
      affiliateName = purchase.getAffiliateCommission().getAffiliate().getFullName();
      affiliateEmail = purchase.getAffiliateCommission().getAffiliate().getEmail();
      affiliateId = purchase.getAffiliateCommission().getAffiliate().getId();
    }

    this.userId = user.getId();
    this.userEmail = user.getEmail();
    this.userName = user.getFullName();
    this.affiliate = affiliateName;
    this.affiliateId = affiliateId;
    this.affiliateEmail = affiliateEmail;
    this.travelerfirstName = primaryTraveler.getFirstName();
    this.travelerlastName = primaryTraveler.getLastName();
    this.traveler = StringUtils.EMPTY;
    this.traveler =
        String.format(
                "%s %s %s",
                StringUtils.trimToEmpty(primaryTraveler.getFirstName()),
                StringUtils.trimToEmpty(primaryTraveler.getMiddleInitials()),
                StringUtils.trimToEmpty(primaryTraveler.getLastName()))
            .trim();
    this.vendor = policyMeta.getVendor().getName();
    this.vendorId = policyMeta.getVendor().getId();
    this.policy = policyMeta.getDisplayName();
    this.policyId = policyMeta.getId();
    this.policyNumber = purchase.getPolicyNumber();
    this.purchaseDate = DateUtil.getLocalDateStr(purchase.getPurchaseDate());
    this.totalPrice = purchase.getTotalPrice().toString();
    this.note = purchase.getNote();
    this.id = purchase.getPurchaseUuid();
  }

  public DataTableJson(SalaryCorrection correction) {
    this.id = String.valueOf(correction.getId());
    this.affiliate = correction.getAffiliate().getFullName();
    this.affiliateId = correction.getAffiliate().getId();
    this.affiliateEmail = correction.getAffiliate().getEmail();
    this.purchaseDate = DateUtil.getLocalDateStr(LocalDate.now());
  }
}