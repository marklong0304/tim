package com.travelinsurancemaster.services.datatable.report;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.json.datatable.payment.PaymentDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.payment.report.PaymentReportDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.purchase.PaymentFilter;
import com.travelinsurancemaster.model.dto.report.PaymentReportFilter;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.datatable.PaymentService;
import com.travelinsurancemaster.services.security.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentsReportTableService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PolicyMetaService policyMetaService;

    public PaymentReportFilter getPaymentReportFilter(PaymentReportDataTableJsonRequest request) {
        PaymentReportFilter paymentReportFilter = new PaymentReportFilter();
        for (Long userId : CollectionUtils.emptyIfNull(request.getAffiliates())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                paymentReportFilter.getAffiliates().add(user.get());
            }
        }
        for (Long userId : CollectionUtils.emptyIfNull(request.getUsers())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                paymentReportFilter.getUsers().add(user.get());
            }
        }
        for (Long vendorId : CollectionUtils.emptyIfNull(request.getVendors())) {
            Optional<Vendor> vendor = Optional.ofNullable(vendorService.getById(vendorId));
            if (vendor.isPresent()) {
                paymentReportFilter.getVendors().add(vendor.get());
            }
        }
        for (Long policyId : CollectionUtils.emptyIfNull(request.getPolicies())) {
            Optional<PolicyMeta> policy = Optional.ofNullable(policyMetaService.getCached(policyId));
            if (policy.isPresent()) {
                paymentReportFilter.getPolicies().add(policy.get());
            }
        }
        paymentReportFilter.setDepartDate(request.getDepartDate().getFrom(), request.getDepartDate().getTo());
        paymentReportFilter.setPurchaseDate(request.getPurchaseDate().getFrom(), request.getPurchaseDate().getTo());
        paymentReportFilter.setTripCost(request.getTripCost().getFrom(), request.getTripCost().getTo());
        paymentReportFilter.setPolicyPrice(request.getPolicyPrice().getFrom(), request.getPolicyPrice().getTo());
        paymentReportFilter.setPolicyNumber(request.getPolicyNumber());
        paymentReportFilter.setNote(request.getNote());
        paymentReportFilter.setTraveler(request.getTraveler());
        paymentReportFilter.setCancellation(request.getCancellation());
        if (!request.getSearch().getValue().isEmpty()) {
            paymentReportFilter.setSearchKeyword(request.getSearch().getValue());
        }

        return paymentReportFilter;
    }
}
