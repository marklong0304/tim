package com.travelinsurancemaster.model.dto.json.datatable.payment.report;

import com.travelinsurancemaster.model.dto.json.datatable.DataTableJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.purchase.PurchaseQuoteRequest;
import com.travelinsurancemaster.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReportJson extends DataTableJson {
    private String tripCost;
    private String departDate;

    public PaymentReportJson(Purchase purchase) {
        super(purchase);
        PurchaseQuoteRequest purchaseQuoteRequest = purchase.getPurchaseQuoteRequest();

        this.tripCost = purchaseQuoteRequest.getTripCost().toString();
        this.departDate = DateUtil.getLocalDateStr(purchaseQuoteRequest.getDepartDate());
    }
}
