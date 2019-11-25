package com.travelinsurancemaster.services.datatable.report;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.json.datatable.commission.CommissionJson;
import com.travelinsurancemaster.model.dto.json.datatable.commission.report.CommissionReportDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.commission.report.CommissionReportJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.report.CommissionReportFilter;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.SecurityHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Chernov Artur on 30.09.15.
 */

@Service
public class CommissionReportTableService {
    private static final Logger log = LoggerFactory.getLogger(CommissionReportTableService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PolicyMetaService policyMetaService;

    public CommissionReportFilter getCommissionReportFilter(CommissionReportDataTableJsonRequest request) {
        CommissionReportFilter commissionReportFilter = new CommissionReportFilter();
        for (Long userId : CollectionUtils.emptyIfNull(request.getAffiliates())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                commissionReportFilter.getAffiliates().add(user.get());
            }
        }
        for (Long userId : CollectionUtils.emptyIfNull(request.getUsers())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                commissionReportFilter.getUsers().add(user.get());
            }
        }
        for (Long vendorId : CollectionUtils.emptyIfNull(request.getVendors())) {
            Optional<Vendor> vendor = Optional.ofNullable(vendorService.getById(vendorId));
            if (vendor.isPresent()) {
                commissionReportFilter.getVendors().add(vendor.get());
            }
        }
        for (Long policyId : CollectionUtils.emptyIfNull(request.getPolicies())) {
            Optional<PolicyMeta> policy = Optional.ofNullable(policyMetaService.getCached(policyId));
            if (policy.isPresent()) {
                commissionReportFilter.getPolicies().add(policy.get());
            }
        }
        commissionReportFilter.setTotalPrice(request.getTotalPrice().getFrom(), request.getTotalPrice().getTo());
        commissionReportFilter.setDepartDate(request.getDepartDate().getFrom(), request.getDepartDate().getTo());
        commissionReportFilter.setPurchaseDate(request.getPurchaseDate().getFrom(), request.getPurchaseDate().getTo());
        commissionReportFilter.setPolicyNumber(request.getPolicyNumber());
        commissionReportFilter.setNote(request.getNote());
        commissionReportFilter.setTraveler(request.getTraveler());
        if (!request.getSearch().getValue().isEmpty()) {
            commissionReportFilter.setSearchKeyword(request.getSearch().getValue());
        }
        commissionReportFilter.setReceivedCommission(request.getReceivedCommission().getFrom(), request.getReceivedCommission().getTo());
        commissionReportFilter.setConfirm(request.getConfirm());
        commissionReportFilter.setExpectedCommission(request.getExpectedCommission().getFrom(), request.getExpectedCommission().getTo());
        commissionReportFilter.setReceivedDate(request.getReceivedDate().getFrom(), request.getReceivedDate().getTo());
        commissionReportFilter.setCheckNumber(request.getCheckNumber());
        commissionReportFilter.setCancellation(request.isCancellation());
        return commissionReportFilter;
    }

    public void fillEditable(CommissionReportJson commissionReportJson, Purchase purchase) {
        commissionReportJson.setEditable((!(purchase.getAffiliateCommission() != null && purchase.getAffiliateCommission().getPaid() != null) && hasEditablePermissions()));
    }

    private boolean hasEditablePermissions() {
        User currentUser = SecurityHelper.getCurrentUser();
        return currentUser != null && (currentUser.hasRole(Role.ROLE_ACCOUNTANT) || currentUser.hasRole(Role.ROLE_ADMIN));
    }
}
