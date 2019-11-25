package com.travelinsurancemaster.model.dto.json.datatable.filter;

import com.travelinsurancemaster.model.security.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AffiliateJson {
  private String affiliateEmail;
  private String affiliateName;
  private Long affiliateId;

  public AffiliateJson(User user) {
    String userLastName =
        user.getUserInfo().getLastName() == null ? "" : user.getUserInfo().getLastName();
    String userCompany =
        user.getUserInfo().getCompanyName().equals("-")
            ? ""
            : String.format("(%s)", user.getUserInfo().getCompanyName());
    this.affiliateEmail = user.getEmail();
    this.affiliateName =
        String.format("%s %s %s", user.getName(), userLastName, userCompany).trim();

    this.affiliateId = user.getId();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof AffiliateJson)) return false;
    if (o == this) return true;
    AffiliateJson c = (AffiliateJson) o;
    return (c.affiliateId.equals(this.affiliateId));
  }
}
