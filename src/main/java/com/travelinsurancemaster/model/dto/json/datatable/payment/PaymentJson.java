package com.travelinsurancemaster.model.dto.json.datatable.payment;

import com.travelinsurancemaster.model.dto.json.datatable.DataTableJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.purchase.PurchaseQuoteRequest;;
import com.travelinsurancemaster.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentJson extends DataTableJson {

  private String tripCost;
  private String departDate;
  private boolean canDelete;

  public PaymentJson(Purchase purchase) {
    super(purchase);
    PurchaseQuoteRequest purchaseQuoteRequest = purchase.getPurchaseQuoteRequest();
    this.tripCost = purchaseQuoteRequest.getTripCost().toString();
    this.departDate = DateUtil.getLocalDateStr(purchaseQuoteRequest.getDepartDate());
    this.canDelete = purchase.getCanDelete();
  }
}
