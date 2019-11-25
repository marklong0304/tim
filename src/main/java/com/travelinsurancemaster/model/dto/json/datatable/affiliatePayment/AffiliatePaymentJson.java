package com.travelinsurancemaster.model.dto.json.datatable.affiliatePayment;

import com.travelinsurancemaster.model.PaymentOption;
import com.travelinsurancemaster.model.dto.AffiliatePayment;
import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AffiliatePaymentJson {

  private String id;
  private String paymentDate;
  private Long affiliateUserId;
  private String affiliateUser;
  private Long affiliateCompanyId;
  private String affiliateCompany;
  private String paymentOption;
  private String bankName;
  private String bankRouting;
  private String account;
  private String paypalEmailAddress;
  private String total;

  public AffiliatePaymentJson(AffiliatePayment affiliatePayment) {
    this.id = String.valueOf(affiliatePayment.getId());
    this.paymentDate = DateUtil.getLocalDateStr(affiliatePayment.getPaymentDate());
    User affiliateUser = affiliatePayment.getAffiliateUser();
    if(affiliateUser != null) {
      this.affiliateUserId = affiliateUser.getId();
      this.affiliateUser = affiliateUser.getFullName();
    }
    Company affiliateCompany = affiliatePayment.getAffiliateCompany();
    if(affiliateCompany != null) {
      this.affiliateCompanyId = affiliateCompany.getId();
      this.affiliateCompany = affiliateCompany.getName();
    }
    PaymentOption paymentOption = affiliatePayment.getPaymentOption();
    if(paymentOption != null) {
        this.paymentOption = paymentOption.getValue();
        this.bankName = affiliatePayment.getBankName();
        this.bankRouting = affiliatePayment.getBankRouting();
        this.account = affiliatePayment.getAccount();
        this.paypalEmailAddress = affiliatePayment.getPaypalEmailAddress();
    }
    total = String.valueOf(affiliatePayment.getTotal());
  }
}