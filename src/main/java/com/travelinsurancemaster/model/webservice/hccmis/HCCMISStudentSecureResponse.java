package com.travelinsurancemaster.model.webservice.hccmis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ritchie on 3/17/15.
 */
public class HCCMISStudentSecureResponse {
    @JsonProperty("Key")
    private String key;
    @JsonProperty("Value")
    List<String> values;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
