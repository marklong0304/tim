package com.travelinsurancemaster.model.dto.json.datatable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.travelinsurancemaster.model.dto.AffiliatePayment;
import com.travelinsurancemaster.model.dto.json.datatable.filter.FilterOptions;
import com.travelinsurancemaster.model.dto.json.datatable.filter.PolicyJson;
import com.travelinsurancemaster.model.dto.json.datatable.filter.VendorJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class AbstractDataTableJsonResponse {
    private int recordsTotal;
    private int recordsFiltered;
    private String error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FilterOptions filterOptions;

    public void sendFilterOptions(List<Purchase> purchasesList) {
    this.filterOptions = new FilterOptions(purchasesList);
    }

    public void sendFilterOptionsJson(List<? extends DataTableJson> purchasesList) {
        List<VendorJson> vendors = new ArrayList<>();
        List<PolicyJson> policies = new ArrayList<>();
        this.filterOptions = new FilterOptions(purchasesList, vendors, policies);
    }

    public void sendAffiliatePaymentFilterOptionsJson(List<AffiliatePayment> affiliatePayments) {
        filterOptions = new FilterOptions();
        filterOptions.setAffiliatePaymentFilterOptions(affiliatePayments);
    }
}