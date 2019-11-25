package com.travelinsurancemaster.model.dto.cms.page;

import java.io.Serializable;

/**
 * Created by Chernov Artur on 23.11.15.
 */
public class PolicyMetaCategoryContentResult implements Serializable {
    private String category;
    private String content;
    private String vendorCode;
    private String policyMetaName;

    public PolicyMetaCategoryContentResult(String category, String content, String vendorCode, String policyMetaName) {
        this.category = category;
        this.content = content;
        this.vendorCode = vendorCode;
        this.policyMetaName = policyMetaName;
    }

    public PolicyMetaCategoryContentResult(String category, String content) {
        this.category = category;
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPolicyMetaName() {
        return policyMetaName;
    }

    public void setPolicyMetaName(String policyMetaName) {
        this.policyMetaName = policyMetaName;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }
}
