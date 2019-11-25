package com.travelinsurancemaster.model.dto.json.datatable.filter;

import com.travelinsurancemaster.model.dto.Company;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyJson {

  private Long companyId;
  private String companyName;

  public CompanyJson(Company company) {
    this.companyId = company.getId();
    this.companyName = company.getName();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof CompanyJson)) return false;
    if (o == this) return true;
    CompanyJson c = (CompanyJson) o;
    return (c.companyId.equals(this.companyId));
  }
}