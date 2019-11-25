package com.travelinsurancemaster.model.dto.json.datatable.affiliatePayment;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonResponse;

import java.util.List;

public class AffiliatePaymentDataTableJsonResponse extends AbstractDataTableJsonResponse {

  private List<AffiliatePaymentJson> data;

  public List<AffiliatePaymentJson> getData() {
    return data;
  }

  public void setData(List<AffiliatePaymentJson> data) {
    this.data = data;
  }
}
