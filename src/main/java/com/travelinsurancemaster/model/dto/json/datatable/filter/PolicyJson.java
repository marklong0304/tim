package com.travelinsurancemaster.model.dto.json.datatable.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PolicyJson {
  private String policy;
  private Long policyId;
  private Long vendorId;

  public PolicyJson(String policy, Long policyId, Long vendorId) {
    this.policy = policy;
    this.policyId = policyId;
    this.vendorId = vendorId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof PolicyJson)) return false;
    if (o == this) return true;
    PolicyJson c = (PolicyJson) o;
    return (c.policyId.equals(this.policyId));
  }
}
