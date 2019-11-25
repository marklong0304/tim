package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.commission.VendorCommissionUpload;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 15.09.15.
 */

@Service
@Transactional
public class UploadCommissionService {
    private static final Logger log = LoggerFactory.getLogger(UploadCommissionService.class);

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private VendorService vendorService;

    public List<VendorCommissionUpload> create(String data) {
        String[] rows = data.split("\\r?\\n");
        List<VendorCommissionUpload> vendorCommissionUploads = new ArrayList<>();
        for (String row : rows) {
            String[] columns = row.split("\\s+");
            int checkLength = 0;
            String vendorName ="";
            for (int i = 0; i < columns.length; ++i){
                if (!columns[i].chars().allMatch(Character::isLetter)){
                    checkLength = i;
                    vendorName = vendorName.trim();
                    break;
                } else {
                    vendorName += columns[i] + " ";
                }
            }
            String policyNumber = columns.length > checkLength ? columns[checkLength++] : null;
            String commissionStr = columns.length > checkLength ? columns[checkLength++] : "";
            String checkNumber = columns.length > checkLength ? columns[checkLength] : null;
            VendorCommissionUpload vendorCommissionUpload = new VendorCommissionUpload(vendorName, policyNumber, commissionStr, checkNumber);
            Vendor vendor = vendorService.getByName(vendorName);
            if (vendor == null) {
                vendorCommissionUpload.getErrors().add("Vendor not found");
            }
            List<Purchase> purchases = purchaseService.getByPolicyNumberAndVendorName(policyNumber, vendor.getName());
            if (purchases.size() > 1) {
                log.debug("Multiply purchases by unique vendor and code");
                vendorCommissionUpload.getErrors().add("Multiply purchase by unique vendor and code");
            }
            Purchase purchase = purchases.size() > 0 ? purchases.get(0) : null;
            if (purchase == null) {
                vendorCommissionUpload.getErrors().add("Policy number not found");
            } else {
                if (purchase.getVendorCommission().getReceivedCommission() != null) {
                    vendorCommissionUpload.getErrors().add("Duplicate commission record");
                }
            }
            if (NumberUtils.parseBigDecimal(commissionStr) == null) {
                vendorCommissionUpload.getErrors().add("Commission is not number");
            }
            vendorCommissionUpload.setStatus(vendorCommissionUpload.getErrors().isEmpty() ? "OK" : "ERROR");
            vendorCommissionUpload.setConfirm(vendorCommissionUpload.getErrors().isEmpty());
            if (vendorCommissionUpload.getErrors().isEmpty() ||
                    (vendorCommissionUpload.getErrors().size() == 1 && vendorCommissionUpload.getErrors().get(0).equals("Received commission should be equal expected commission"))) {
                vendorCommissionUpload.setDisabled(false);
            }
            vendorCommissionUploads.add(vendorCommissionUpload);
        }
        return vendorCommissionUploads;
    }

    public boolean check(List<VendorCommissionUpload> vendorCommissionUploads) {
        for (VendorCommissionUpload upload : vendorCommissionUploads) {
            if (!upload.getErrors().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
