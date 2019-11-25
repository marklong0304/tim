package com.travelinsurancemaster.model.dto.json.datatable.filter;

import com.travelinsurancemaster.model.dto.AffiliatePayment;
import com.travelinsurancemaster.model.dto.json.datatable.DataTableJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FilterOptions {
  private List<VendorJson> vendors;
  private List<PolicyJson> policies;

  public FilterOptions() {
    this.vendors = new ArrayList<>();
    this.policies = new ArrayList<>();
  }

  public FilterOptions(List<? extends DataTableJson> purchasesList, List<VendorJson> vendors, List<PolicyJson> policies) {
    this.vendors = vendors;
    this.policies = policies;
    for (DataTableJson purchase : purchasesList) {

      if (purchase.getVendor() != null) {
        VendorJson tempVendor = new VendorJson(purchase.getVendor(), purchase.getVendorId());
        if (!this.vendors.contains(tempVendor)) {
          this.vendors.add(tempVendor);
        }
      }

      if (purchase.getPolicy() != null) {
        PolicyJson tempPolicy =
            new PolicyJson(purchase.getPolicy(), purchase.getPolicyId(), purchase.getVendorId());
        if (!this.policies.contains(tempPolicy)) {
          this.policies.add(tempPolicy);
        }
      }
    }
  }

  public FilterOptions(List<Purchase> purchasesList) {
    this.vendors = new ArrayList<>();
    this.policies = new ArrayList<>();
    purchasesList.forEach(p -> addPurchase(p));
  }

  public void setAffiliatePaymentFilterOptions(List<AffiliatePayment> affiliatePayments) {
    vendors = new ArrayList<>();
    policies = new ArrayList<>();
    affiliatePayments.forEach(ap -> ap.getAffiliateCommissions().forEach(ac -> addPurchase(ac.getPurchase())));
  }

  private void addPurchase(Purchase purchase) {
    if (purchase.getPolicyMeta().getVendor() != null) {
      VendorJson tempVendor =
              new VendorJson(
                      purchase.getPolicyMeta().getVendor().getName(),
                      purchase.getPolicyMeta().getVendor().getId());
      if (!this.vendors.contains(tempVendor)) {
        this.vendors.add(tempVendor);
      }
    }
    if (purchase.getPolicyMeta() != null) {
      PolicyJson tempPolicy =
              new PolicyJson(
                      purchase.getPolicyMeta().getDisplayName(),
                      purchase.getPolicyMeta().getId(),
                      purchase.getPolicyMeta().getVendor().getId());
      if (!this.policies.contains(tempPolicy)) {
        this.policies.add(tempPolicy);
      }
    }
  }
}