package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Field {

    private String code;
    private String value;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
