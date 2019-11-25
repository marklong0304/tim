package com.travelinsurancemaster.model.security;

/**
 * Created by Chernov Artur on 17.04.15.
 */

public enum Role {
    ROLE_USER ("Consumer"),
    ROLE_AFFILIATE("Affiliate"),
    ROLE_COMPANY_MANAGER("Company Manager"),
    ROLE_CUSTOMER_SERVICE("Customer Service"),
    ROLE_ACCOUNTANT("Accountant"),
    ROLE_API("API"),
    ROLE_CONTENT_MANAGER("Content manager"),
    ROLE_ADMIN("Admin");

    private String caption;

    Role(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}