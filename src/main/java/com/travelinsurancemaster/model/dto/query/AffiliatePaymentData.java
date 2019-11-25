package com.travelinsurancemaster.model.dto.query;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Raman on 11.07.19.
 */

@Data
public class AffiliatePaymentData implements IAffiliatePaymentData {
    private String purchaseUuid;
    private Date purchaseDate;
    private Boolean cancelled;
    private String vendorName;
    private String policyNumber;
    private String displayName;
    private BigDecimal salaryToPay;
    private BigDecimal salaryPaid;
    private String note;
    private Date paymentDate;
    private Long paymentId;
}