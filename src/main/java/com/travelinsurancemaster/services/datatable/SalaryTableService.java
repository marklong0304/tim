package com.travelinsurancemaster.services.datatable;

import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.json.datatable.salary.SalaryDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.salary.SalaryJson;
import com.travelinsurancemaster.model.dto.json.datatable.salary.SalarySearchDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.SalaryCorrection;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryReportJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.purchase.SalaryFilter;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.util.datatable.DataTableField;
import com.travelinsurancemaster.services.AffiliatePaymentService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.datatable.report.SalaryReportService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.NumberUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/** Created by Chernov Artur on 10.09.15. */
@Service
public class SalaryTableService {
  private static final Logger log = LoggerFactory.getLogger(SalaryTableService.class);

  @Autowired private UserService userService;

  @Autowired private VendorService vendorService;

  @Autowired private PurchaseService purchaseService;

  @Autowired private PolicyMetaService policyMetaService;

  @Autowired private SalaryReportService salaryReportService;

  @Autowired private AffiliatePaymentService affiliatePaymentService;

  public SalaryFilter getSalaryFilter(SalaryDataTableJsonRequest request) {
    SalaryFilter salaryFilter = new SalaryFilter();
    for (Long userId : CollectionUtils.emptyIfNull(request.getAffiliates())) {
      Optional<User> user = Optional.ofNullable(userService.get(userId));
      if (user.isPresent()) {
        salaryFilter.getAffiliates().add(user.get());
      }
    }
    for (Long vendorId : CollectionUtils.emptyIfNull(request.getVendors())) {
      Optional<Vendor> vendor = Optional.ofNullable(vendorService.getById(vendorId));
      if (vendor.isPresent()) {
        salaryFilter.getVendors().add(vendor.get());
      }
    }
    for (Long policyId : CollectionUtils.emptyIfNull(request.getPolicies())) {
      Optional<PolicyMeta> policy = Optional.ofNullable(policyMetaService.getCached(policyId));
      if (policy.isPresent()) {
        salaryFilter.getPolicies().add(policy.get());
      }
    }
    salaryFilter.setPurchaseDate(
        request.getPurchaseDate().getFrom(), request.getPurchaseDate().getTo());
    salaryFilter.setPolicyNumber(request.getPolicyNumber());
    salaryFilter.setNote(request.getNote());
    salaryFilter.setTraveler(request.getTraveler());
    if (!request.getSearch().getValue().isEmpty()) {
      salaryFilter.setSearchKeyword(request.getSearch().getValue());
    }
    salaryFilter.setPay(request.isPay());
    salaryFilter.setExpectedSalary(
        request.getExpectedSalary().getFrom(), request.getExpectedSalary().getTo());
    salaryFilter.setReceivedCommission(
        request.getReceivedCommission().getFrom(), request.getReceivedCommission().getTo());
    salaryFilter.setTotalPrice(request.getTotalPrice().getFrom(), request.getTotalPrice().getTo());
    salaryFilter.setSalary(request.getSalary().getFrom(), request.getSalary().getTo());
    salaryFilter.setCancellation(request.isCancellation());
    return salaryFilter;
  }

    public SalaryFilter getSalaryFilter(SalarySearchDataTableJsonRequest request) {
        SalaryFilter salaryFilter = new SalaryFilter();
        for (Long userId : CollectionUtils.emptyIfNull(request.getAffiliates())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                salaryFilter.getAffiliates().add(user.get());
            }
        }
        if (!request.getSearch().getValue().isEmpty()) {
            salaryFilter.setSearchKeyword(request.getSearch().getValue());
        }
        salaryFilter.setPayDate(request.getPayDate().getFrom(), request.getPayDate().getTo());
        salaryFilter.setSalary(request.getSalary().getFrom(), request.getSalary().getTo());
        return salaryFilter;
    }

  public void updateSalary(
          DataTableField field,
          SalaryFilter salaryFilter,
          Map<Long, AffiliatePayment> affiliateUserPayments,
          Map<Long, AffiliatePayment> affiliateCompanyPayments,
          Map<String, SalaryCorrection> balanceSalaryCorrections
  ) {
    if (!hasEditablePermissions()) {
      return;
    }
    if (Objects.equals(field.getName(), "payAll")) {
      updateAllPay(field.getId(), Boolean.valueOf(field.getValue()), salaryFilter);
    } else {

      if (field.getId() == null) {
        return;
      }

      Purchase purchase = purchaseService.getPurchase(field.getId());
      if (purchase != null) {
        switch (field.getName()) {
          case "pay":
            updatePay(purchase, field.getValue(), field.getSalary(), affiliateUserPayments, affiliateCompanyPayments);
            break;
          case "salary":
            updateSalary(purchase, field.getValue());
            break;
          case "note":
            updateNote(purchase, field.getValue());
            break;
        }
        purchaseService.save(purchase);
        return;
      }

      SalaryCorrection salaryCorrection = salaryReportService.getSalaryCorrection(field.getId());
      if(salaryCorrection == null) {
        salaryCorrection = balanceSalaryCorrections.get(field.getId());
        if(salaryCorrection == null && "pay".equals(field.getName()) && Boolean.valueOf(field.getValue())) {
          Long affiliateId = NumberUtils.parseLong(field.getAffiliateId());
          if(affiliateId != null) {
            User userAffiliate = userService.get(affiliateId);
            salaryCorrection = new SalaryCorrection(userAffiliate);
            salaryCorrection.setSalaryToPay(new BigDecimal(0));
            salaryCorrection.setNote("Adjustment for balance on " + DateUtil.getDateStr(salaryCorrection.getReceivedDate()));
            balanceSalaryCorrections.put(field.getId(), salaryCorrection);
          }
        }
      }
      if(salaryCorrection != null) {
        switch (field.getName()) {
          case "pay":
            updatePay(salaryCorrection, field.getValue(), field.getSalary(), affiliateUserPayments, affiliateCompanyPayments);
            break;
          case "salary":
            updateSalary(salaryCorrection, field.getValue());
            break;
          case "note":
            updateNote(salaryCorrection, field.getValue());
            break;
        }
        salaryReportService.save(salaryCorrection);
      }
    }
  }

  private void updateNote(Purchase purchase, String value) {
    purchase.setNote(value);
  }

  private void updateSalary(Purchase purchase, String value) {
    BigDecimal salary = null;
    if (!value.isEmpty()) {
      salary = new BigDecimal(value);
    }
    AffiliateCommission affiliateCommission = purchase.getAffiliateCommission();
    affiliateCommission.setSalaryToPay(salary);
  }

  private void fillAffiliatePayment(
          IAffiliateSinglePayment affiliateSinglePayment,
          Map<Long, AffiliatePayment> affiliateUserPayments,
          Map<Long, AffiliatePayment> affiliateCompanyPayments
  ) {
    AffiliatePayment affiliatePayment = null;
    User affiliateUser = affiliateSinglePayment.getAffiliate();
    if(affiliateUser != null) {
      Company affiliateCompany = affiliateUser.getCompany();
      if(affiliateCompany != null) {
        //Affiliate belongs to a company
        affiliatePayment = affiliateCompanyPayments.get(affiliateCompany.getId());
        if(affiliatePayment == null) {
          affiliatePayment = new AffiliatePayment(affiliateCompany);
          affiliatePayment.setStatusPaid(LocalDate.now());
          affiliatePayment = affiliatePaymentService.save(affiliatePayment);
          affiliateCompanyPayments.put(affiliateCompany.getId(), affiliatePayment);
        }
      } else if(affiliateUser.isAffiliate()) {
        //Affiliate is an individual
        affiliatePayment = affiliateUserPayments.get(affiliateUser.getId());
        if(affiliatePayment == null) {
          affiliatePayment = new AffiliatePayment(affiliateUser);
          affiliatePayment.setStatusPaid(LocalDate.now());
          affiliatePayment = affiliatePaymentService.save(affiliatePayment);
          affiliateUserPayments.put(affiliateUser.getId(), affiliatePayment);
        }
      }
    }
    if(affiliatePayment != null) {
      affiliateSinglePayment.setAffiliatePayment(affiliatePayment);
      affiliateSinglePayment.setSalaryPaid(affiliateSinglePayment.getSalaryToPay());
      affiliateSinglePayment.setPaid(new Date());
      if(affiliateSinglePayment.getSalaryPaid() != null) {
        if(affiliatePayment.getTotal() != null) {
          affiliatePayment.setTotal(affiliatePayment.getTotal().add(affiliateSinglePayment.getSalaryPaid()));
        } else {
          affiliatePayment.setTotal(affiliateSinglePayment.getSalaryPaid());
        }
      }
    }
  }

  private void updatePay(
          Purchase purchase,
          String value,
          String salary,
          Map<Long, AffiliatePayment> affiliateUserPayments,
          Map<Long, AffiliatePayment> affiliateCompanyPayments
  ) {
    boolean pay = Boolean.valueOf(value);
    if(pay) {
      AffiliateCommission affiliateCommission = purchase.getAffiliateCommission();
      if(affiliateCommission.getSalaryToPay().compareTo(BigDecimal.ZERO) == 0) {
        affiliateCommission.setSalaryToPay(new BigDecimal(salary));
      }
      fillAffiliatePayment(affiliateCommission, affiliateUserPayments, affiliateCompanyPayments);
    }
  }

  private void updateNote(SalaryCorrection salaryCorrection, String value) {
    salaryCorrection.setNote(value);
  }

  private void updateSalary(SalaryCorrection salaryCorrection, String value) {
    BigDecimal salary = null;
    if (!value.isEmpty()) {
      salary = new BigDecimal(value);
    }
    salaryCorrection.setSalaryToPay(salary);
    //Set salary paid when salary correction is being created
    if(salaryCorrection.getSalaryPaid() == null || salaryCorrection.getSalaryPaid().compareTo(BigDecimal.ZERO) == 0) {
      salaryCorrection.setSalaryPaid(salary);
    }
  }

  private void updatePay(
          SalaryCorrection salaryCorrection,
          String value,
          String salary,
          Map<Long, AffiliatePayment> affiliateUserPayments,
          Map<Long, AffiliatePayment> affiliateCompanyPayments
  ) {
    boolean pay = Boolean.valueOf(value);
    if(pay) {
      if(salaryCorrection.getSalaryToPay().compareTo(BigDecimal.ZERO) == 0) {
        salaryCorrection.setSalaryToPay(new BigDecimal(salary));
      }
      fillAffiliatePayment(salaryCorrection, affiliateUserPayments, affiliateCompanyPayments);
    }
  }

  private void updateAllPay(String affiliateId, boolean status, SalaryFilter salaryFilterParam) {
    User user = userService.get(NumberUtils.parseLong(affiliateId));
    SalaryFilter salaryFilter = SerializationUtils.clone(salaryFilterParam);
    salaryFilter.getAffiliates().clear();
    salaryFilter.getAffiliates().add(user);
    List<Purchase> purchases = purchaseService.getAllSuccessBySalaryFilter(salaryFilter);
    for (Purchase purchase : purchases) {
      purchase.getAffiliateCommission().setPaid(status ? new Date() : null);
      purchaseService.save(purchase);
    }
  }

  public void fillPayAll(SalaryJson salaryJson, SalaryFilter salaryFilterParam, Purchase purchase) {
    User user = purchase.getAffiliateCommission().getAffiliate();
    SalaryFilter salaryFilter = salaryFilterParam;
    salaryFilter.getAffiliates().clear();
    salaryFilter.getAffiliates().add(user);
    salaryFilter.setPay(false);
    salaryJson.setPayAll(
        CollectionUtils.isEmpty(purchaseService.getAllSuccessBySalaryFilter(salaryFilter)));
  }

    public void fillEditable(SalaryJson salaryJson) {
        salaryJson.setEditable((hasEditablePermissions()));
    }

    private boolean hasEditablePermissions() {
        User currentUser = SecurityHelper.getCurrentUser();
        return currentUser != null && (currentUser.hasRole(Role.ROLE_ACCOUNTANT) || currentUser.hasRole(Role.ROLE_ADMIN));
    }
}