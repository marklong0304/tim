package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.security.User;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Raman on 25.06.19.
 */

public interface IAffiliateSinglePayment {

    public Long getId();
    public void setId(Long id);

    public User getAffiliate();
    public void setAffiliate(User affiliateUser);

    public AffiliatePayment getAffiliatePayment();
    public void setAffiliatePayment(AffiliatePayment affiliatePayment);

    public BigDecimal getSalaryToPay();
    public void setSalaryToPay(BigDecimal salaryToPay);

    public BigDecimal getSalaryPaid();
    public void setSalaryPaid(BigDecimal salaryPaid);

    public Date getPaid();
    public void setPaid(Date paidDate);
}