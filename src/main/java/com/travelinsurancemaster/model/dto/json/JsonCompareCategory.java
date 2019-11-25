package com.travelinsurancemaster.model.dto.json;

/**
 * Created by Chernov Artur on 18.09.2016.
 */
public class JsonCompareCategory {
    private String name;
    private String code;

    public JsonCompareCategory(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
