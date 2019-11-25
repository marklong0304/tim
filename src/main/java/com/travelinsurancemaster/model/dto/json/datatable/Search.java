package com.travelinsurancemaster.model.dto.json.datatable;

/**
 * Created by Chernov Artur on 20.08.15.
 */
public class Search {
    private String value;
    private boolean regex;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }
}