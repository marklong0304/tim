package com.travelinsurancemaster.model.dto.commission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 15.09.15.
 */
public class VendorCommissionUpload {
    private String vendor;
    private String policyNumber;
    private String receivedCommission;
    private String checkNumber;
    private String status;
    private Boolean confirm = false;
    private Boolean disabled = true;
    private List<String> errors = new ArrayList<>();

    public VendorCommissionUpload() {
    }

    public VendorCommissionUpload(String vendor, String policyNumber, String receivedCommission, String checkNumber) {
        this.vendor = vendor;
        this.policyNumber = policyNumber;
        this.receivedCommission = receivedCommission;
        this.checkNumber = checkNumber;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getReceivedCommission() {
        return receivedCommission;
    }

    public void setReceivedCommission(String receivedCommission) {
        this.receivedCommission = receivedCommission;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Boolean getConfirm() {
        return confirm;
    }

    public Boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }

    public Boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
