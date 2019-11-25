package com.travelinsurancemaster.model.dto.json.datatable.filter;

import com.travelinsurancemaster.model.security.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserJson {
  private String userEmail;
  private String userName;
  private Long userId;

  public UserJson(User user) {
    String userLastName =
        user.getUserInfo().getLastName() == null ? "" : user.getUserInfo().getLastName();
    this.userEmail = user.getEmail();
    this.userName = String.format("%s %s", user.getName(), userLastName).trim();
    this.userId = user.getId();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof UserJson)) return false;
    if (o == this) return true;
    UserJson c = (UserJson) o;
    return (c.userId.equals(this.userId));
  }
}
