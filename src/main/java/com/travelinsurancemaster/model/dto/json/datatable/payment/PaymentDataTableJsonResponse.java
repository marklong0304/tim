package com.travelinsurancemaster.model.dto.json.datatable.payment;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonResponse;
import com.travelinsurancemaster.model.dto.json.datatable.DataTableJson;

import java.util.List;
import java.util.ArrayList;

/** Created by Chernov Artur on 18.08.15. */
public class PaymentDataTableJsonResponse extends AbstractDataTableJsonResponse {

  private List<PaymentJson> data;

  public List<PaymentJson> getData() {
    return data;
  }

  public void setData(List<PaymentJson> data) {
    this.data = data;
  }
}
