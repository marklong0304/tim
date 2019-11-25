package com.travelinsurancemaster.services.datatable;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.json.datatable.payment.PaymentDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.purchase.PaymentFilter;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.security.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Chernov Artur on 20.08.15.
 */

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PolicyMetaService policyMetaService;

    public PaymentFilter getPaymentFilter(PaymentDataTableJsonRequest request) {
        PaymentFilter paymentFilter = new PaymentFilter();
        for (Long userId : CollectionUtils.emptyIfNull(request.getAffiliates())) {
            if (userId == -1) {
                paymentFilter.setNoAffiliate(true);
            }
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                paymentFilter.getAffiliates().add(user.get());
            }
        }
        for (Long userId : CollectionUtils.emptyIfNull(request.getUsers())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                paymentFilter.getUsers().add(user.get());
            }
        }
        for (Long vendorId : CollectionUtils.emptyIfNull(request.getVendors())) {
            Optional<Vendor> vendor = Optional.ofNullable(vendorService.getById(vendorId));
            if (vendor.isPresent()) {
                paymentFilter.getVendors().add(vendor.get());
            }
        }
        for (Long policyId : CollectionUtils.emptyIfNull(request.getPolicies())) {
            Optional<PolicyMeta> policy = Optional.ofNullable(policyMetaService.getCached(policyId));
            if (policy.isPresent()) {
                paymentFilter.getPolicies().add(policy.get());
            }
        }
        paymentFilter.setDepartDate(request.getDepartDate().getFrom(), request.getDepartDate().getTo());
        paymentFilter.setPurchaseDate(request.getPurchaseDate().getFrom(), request.getPurchaseDate().getTo());
        paymentFilter.setTripCost(request.getTripCost().getFrom(), request.getTripCost().getTo());
        paymentFilter.setPolicyPrice(request.getPolicyPrice().getFrom(), request.getPolicyPrice().getTo());
        paymentFilter.setPolicyNumber(request.getPolicyNumber());
        paymentFilter.setNote(request.getNote());
        paymentFilter.setTraveler(request.getTraveler());
        paymentFilter.setCancellation(request.getCancellation());
        if (!request.getSearch().getValue().isEmpty()) {
            paymentFilter.setSearchKeyword(request.getSearch().getValue());
        }

        return paymentFilter;
    }


}
