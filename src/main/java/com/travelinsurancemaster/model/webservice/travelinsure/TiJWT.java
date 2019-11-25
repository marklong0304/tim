package com.travelinsurancemaster.model.webservice.travelinsure;

import java.util.Date;

public class TiJWT {

    private String jwt;
    private Date expired;

    public TiJWT(String jwt, Integer expiresIn) {
        this.jwt = jwt;
        this.expired = new Date();
        this.expired.setTime(expired.getTime() + expiresIn * 1000);
    }

    public boolean isExpired() {
        return new Date().getTime() > expired.getTime();
    }

    public String getJwt() {
        return jwt;
    }

}
