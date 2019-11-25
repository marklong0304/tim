package com.travelinsurancemaster.model.webservice.bhtravelprotection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.Base64;

/**
 * Created by maleev on 30.04.2016.
 */


public class BhtpJWT {
    private static final Logger log = LoggerFactory.getLogger(BhtpJWT.class);
    private static ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
    private long exp;
    private long iat;
    private String jwt;

    public BhtpJWT(String jwt){
        this.jwt = jwt;
        decodeBody();
    }

    private void decodeBody(){
        String[] jwtParts = this.jwt.split("\\.");
        if (jwtParts.length != 3) return;

        byte[] bodyBytes = Base64.getDecoder().decode(jwtParts[1]);
        try {
            BhtpJWTBody jwtBody = objectMapper.readValue(bodyBytes, BhtpJWTBody.class);
            exp = jwtBody.getExp();
            iat = jwtBody.getIat();
        } catch (IOException e) {
            log.error("Token is not get for BHTH");
        }
    }

    public boolean isExpired(){
        return exp == 0 || jwt == null || (System.currentTimeMillis()/1000) > exp;
    }

    public String getJwt() {
        return jwt;
    }
}
