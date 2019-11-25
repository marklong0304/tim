package com.travelinsurancemaster.model.dto.query;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Raman on 11.07.19.
 */

public interface IAffiliatePaymentData {
    String getPurchaseUuid();
    Date getPurchaseDate();
    Boolean getCancelled();
    String getVendorName();
    String getPolicyNumber();
    String getDisplayName();
    BigDecimal getSalaryToPay();
    BigDecimal getSalaryPaid();
    String getNote();
    Date getPaymentDate();
    Long getPaymentId();
}