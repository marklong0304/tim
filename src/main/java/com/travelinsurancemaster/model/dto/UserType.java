package com.travelinsurancemaster.model.dto;

public enum UserType {
    USER("Consumer"),
    COMPANY("Affiliate"),
    EMPLOYEE("Employee");

    private String caption;

    UserType(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

}
