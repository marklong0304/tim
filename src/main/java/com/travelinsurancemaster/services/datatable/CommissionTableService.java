package com.travelinsurancemaster.services.datatable;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.VendorCommission;
import com.travelinsurancemaster.model.dto.json.datatable.commission.CommissionDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.commission.CommissionJson;
import com.travelinsurancemaster.model.dto.purchase.CommissionFilter;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.util.datatable.DataTableField;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.SecurityHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

/**
 * Created by Chernov Artur on 09.09.15.
 */

@SuppressWarnings("ALL")
@Service
public class CommissionTableService {
    private static final Logger log = LoggerFactory.getLogger(CommissionTableService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PolicyMetaService policyMetaService;

    public CommissionFilter getCommissionFilter(CommissionDataTableJsonRequest request) {
        CommissionFilter commissionFilter = new CommissionFilter();
        for (Long userId : CollectionUtils.emptyIfNull(request.getAffiliates())) {
            if (userId == -1) {
                commissionFilter.setNoAffiliate(true);
            }
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                commissionFilter.getAffiliates().add(user.get());
            }
        }
        for (Long userId : CollectionUtils.emptyIfNull(request.getUsers())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                commissionFilter.getUsers().add(user.get());
            }
        }
        for (Long vendorId : CollectionUtils.emptyIfNull(request.getVendors())) {
            Optional<Vendor> vendor = Optional.ofNullable(vendorService.getById(vendorId));
            if (vendor.isPresent()) {
                commissionFilter.getVendors().add(vendor.get());
            }
        }
        for (Long policyId : CollectionUtils.emptyIfNull(request.getPolicies())) {
            Optional<PolicyMeta> policy = Optional.ofNullable(policyMetaService.getCached(policyId));
            if (policy.isPresent()) {
                commissionFilter.getPolicies().add(policy.get());
            }
        }
        commissionFilter.setTotalPrice(request.getTotalPrice().getFrom(), request.getTotalPrice().getTo());
        commissionFilter.setDepartDate(request.getDepartDate().getFrom(), request.getDepartDate().getTo());
        commissionFilter.setPurchaseDate(request.getPurchaseDate().getFrom(), request.getPurchaseDate().getTo());
        commissionFilter.setPolicyNumber(request.getPolicyNumber());
        commissionFilter.setNote(request.getNote());
        commissionFilter.setTraveler(request.getTraveler());
        if (!request.getSearch().getValue().isEmpty()) {
            commissionFilter.setSearchKeyword(request.getSearch().getValue());
        }
        commissionFilter.setReceivedCommission(request.getReceivedCommission().getFrom(), request.getReceivedCommission().getTo());
        commissionFilter.setConfirm(request.getConfirm());
        commissionFilter.setExpectedCommission(request.getExpectedCommission().getFrom(), request.getExpectedCommission().getTo());
        commissionFilter.setReceivedDate(request.getReceivedDate().getFrom(), request.getReceivedDate().getTo());
        commissionFilter.setCheckNumber(request.getCheckNumber());
        commissionFilter.setCancellation(request.isCancellation());
        return commissionFilter;
    }

    public void updateCommission(DataTableField field) {
        if (!hasEditablePermissions()) {
            return;
        }
        if (field.getId() == null) {
            return;
        }
        Purchase purchase = purchaseService.getPurchase(field.getId());
        if (purchase == null) {
            return;
        }
        switch (field.getName()) {
            case "confirm":
                updateConfirm(purchase, field.getValue());
                break;
            case "checkNumber":
                updateCheckNumber(purchase, field.getValue());
                break;
            case "receivedCommission":
                updateReceivedCommission(purchase, field.getValue());
                break;
            case "receivedDate":
                updateReceivedDate(purchase, field.getValue());
                break;
            case "note":
                updateNote(purchase, field.getValue());
                break;
        }
        purchaseService.save(purchase);
    }

    private void updateReceivedDate(Purchase purchase, String value) {
        Date receivedDate = DateUtil.getDate(value);
        VendorCommission vendorCommission = purchase.getVendorCommission();
        vendorCommission.setReceivedDate(receivedDate);
    }

    private void updateNote(Purchase purchase, String value) {
        purchase.setNote(value);
    }

    private void updateReceivedCommission(Purchase purchase, String value) {
        BigDecimal receivedCommission = null;
        if (!value.isEmpty()) {
            receivedCommission = new BigDecimal(value);
        }
        VendorCommission vendorCommission = purchase.getVendorCommission();
        vendorCommission.setReceivedCommission(receivedCommission);
    }

    private void updateCheckNumber(Purchase purchase, String value) {
        VendorCommission vendorCommission = purchase.getVendorCommission();
        vendorCommission.setCheckNumber(value);
    }

    private void updateConfirm(Purchase purchase, String value) {
        boolean confirm = Boolean.valueOf(value);
        VendorCommission vendorCommission = purchase.getVendorCommission();
        vendorCommission.setConfirm(confirm);
        if(!confirm) {
            vendorCommission.setReceivedCommission(null);
            vendorCommission.setReceivedDate(null);
        }
    }

    public void fillEditable(CommissionJson commissionJson, Purchase purchase) {
        commissionJson.setEditable((!(purchase.getAffiliateCommission() != null && purchase.getAffiliateCommission().getPaid() != null) && hasEditablePermissions()));
    }

    private boolean hasEditablePermissions() {
        User currentUser = SecurityHelper.getCurrentUser();
        return currentUser != null && (currentUser.hasRole(Role.ROLE_ACCOUNTANT) || currentUser.hasRole(Role.ROLE_ADMIN));
    }
}
