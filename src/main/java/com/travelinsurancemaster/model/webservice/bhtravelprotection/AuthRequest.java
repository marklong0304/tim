package com.travelinsurancemaster.model.webservice.bhtravelprotection;


import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by maleev on 29.04.2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuthRequest {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
