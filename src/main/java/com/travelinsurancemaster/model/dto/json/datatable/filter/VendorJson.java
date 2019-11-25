package com.travelinsurancemaster.model.dto.json.datatable.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class VendorJson {
  private String vendor;
  private Long vendorId;

  public VendorJson(String vendor, Long vendorId) {
    this.vendor = vendor;
    this.vendorId = vendorId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof VendorJson)) return false;
    if (o == this) return true;
    VendorJson c = (VendorJson) o;
    return (c.vendor.equals(this.vendor) && Objects.equals(c.vendorId, this.vendorId));
  }
}
