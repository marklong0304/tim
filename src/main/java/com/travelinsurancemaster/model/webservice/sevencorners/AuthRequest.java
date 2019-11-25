package com.travelinsurancemaster.model.webservice.sevencorners;

public class AuthRequest {

    private String clientId;
    private String grantType;

    public AuthRequest() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
