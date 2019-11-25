package com.travelinsurancemaster.model.dto.json.datatable.commission;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonResponse;

import java.util.ArrayList;
import java.util.List;

/** Created by Chernov Artur on 09.09.15. */
public class CommissionDataTableJsonResponse extends AbstractDataTableJsonResponse {
  private List<CommissionJson> data;

  public List<CommissionJson> getData() {
    return data;
  }

  public void setData(List<CommissionJson> data) {
    this.data = data;
  }
}
