package com.travelinsurancemaster.model.dto.json.datatable.commission.report;

import com.travelinsurancemaster.model.dto.VendorCommission;
import com.travelinsurancemaster.model.dto.json.datatable.DataTableJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;

/** Created by Chernov Artur on 30.09.15. */
@Getter
@Setter
public class CommissionReportJson extends DataTableJson {
  private String expectedCommission;
  private String confirm;
  private String totalPrice;
  private String checkNumber;
  private String receivedCommission;
  private String receivedDate;
  private boolean editable = false;

  public CommissionReportJson(Purchase purchase) {
    super(purchase);
    this.totalPrice = purchase.getTotalPrice().toString();
    VendorCommission vendorCommission = purchase.getVendorCommission();
    if (vendorCommission != null) {
      this.expectedCommission = vendorCommission.getExpectedCommission().toString();
      this.confirm = BooleanUtils.toString(vendorCommission.isConfirm(), "true", "false");
      this.checkNumber = vendorCommission.getCheckNumber();
      this.receivedCommission =
          vendorCommission.getReceivedCommission() != null
              ? purchase.getVendorCommission().getReceivedCommission().toString()
              : "";
      this.receivedDate = DateUtil.getDateStr(vendorCommission.getReceivedDate());
    }
  }
}
