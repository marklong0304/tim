package com.travelinsurancemaster.model.dto.commission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 16.09.15.
 */
public class VendorCommissionUploadForm {
    private String data = "";
    private List<VendorCommissionUpload> uploadList = new ArrayList<>();

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<VendorCommissionUpload> getUploadList() {
        return uploadList;
    }

    public void setUploadList(List<VendorCommissionUpload> uploadList) {
        this.uploadList = uploadList;
    }
}
