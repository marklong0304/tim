package com.travelinsurancemaster.model.dto.json.datatable.payment.report;

import com.travelinsurancemaster.model.PaymentCancellationType;
import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.purchase.PurchaseTravelerJson;
import com.travelinsurancemaster.model.util.BigDecimalRange;
import com.travelinsurancemaster.model.util.DateRange;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReportDataTableJsonRequest extends AbstractDataTableJsonRequest {
    private static final long serialVersionUID = 7685924877738833011L;

    private DateRange departDate;
    private DateRange purchaseDate;
    private BigDecimalRange tripCost;
    private BigDecimalRange policyPrice;
    private String policyNumber;
    private String note;
    private PurchaseTravelerJson traveler;
    private PaymentCancellationType cancellation;
}
