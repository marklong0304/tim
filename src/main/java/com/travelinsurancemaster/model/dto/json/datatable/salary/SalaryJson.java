package com.travelinsurancemaster.model.dto.json.datatable.salary;

import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.dto.SalaryCorrection;
import com.travelinsurancemaster.model.dto.json.datatable.DataTableJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.User;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * Created by Chernov Artur on 10.09.15.
 */
public class SalaryJson extends DataTableJson {
    private String expectedSalary;
    private String receivedCommission;
    private String salary;
    private boolean pay;
    private boolean payAll = false;
    private String note;
    private String company;
    private String companyId;
    private boolean editable = false;
    private boolean canDelete = true;

    public SalaryJson(Long id, User affiliateUser, String salary) {
        super(getStringValue(id), affiliateUser.getId(), affiliateUser.getFullName());
        this.expectedSalary = salary;
        this.salary = salary;
        Company company = affiliateUser.getCompany();
        this.company = company != null ? company.getName() : StringUtils.EMPTY;
        this.companyId = company != null ? company.getId().toString() : StringUtils.EMPTY;
    }

    public SalaryJson(Long id, Company affiliateCompany, String salary) {
        super(getStringValue(id), affiliateCompany.getCompanyManager().getId(), affiliateCompany.getCompanyManager().getFullName());
        this.expectedSalary = salary;
        this.salary = salary;
        this.company = affiliateCompany.getName();
        this.companyId = String.valueOf(affiliateCompany.getId());
    }

    public SalaryJson(Purchase purchase) {
        super(purchase);
        BigDecimal expectedSalary = purchase.getAffiliateCommission().getSalary();
        BigDecimal salary = purchase.getAffiliateCommission().getSalaryToPay();

        this.pay = purchase.getAffiliateCommission().getPaid() != null;

        this.expectedSalary = expectedSalary != null ? expectedSalary.toString() : StringUtils.EMPTY;
        this.receivedCommission =
                purchase.getVendorCommission().getReceivedCommission() != null
                        ? purchase.getVendorCommission().getReceivedCommission().toString()
                        : "";
        this.salary = salary != null ? salary.toString() : StringUtils.EMPTY;
        Company company = purchase.getAffiliateCommission().getAffiliate().getCompany();
        this.company = company != null ? company.getName() : StringUtils.EMPTY;
        this.companyId = company != null ? company.getId().toString() : StringUtils.EMPTY;
        this.note= purchase.getNote();
    }

    public SalaryJson(SalaryCorrection correction) {
        super(correction);
        this.pay = correction.getPaid() != null;
        this.note = correction.getNote();
        this.salary = correction.getSalaryToPay().toString();
        Company company = correction.getAffiliate().getCompany();
        this.company = company != null ? company.getName() : StringUtils.EMPTY;
        this.companyId = company != null ? company.getId().toString() : StringUtils.EMPTY;
        this.canDelete = correction.getCanDelete();
    }

    public String getExpectedSalary() {
        return expectedSalary;
    }
    public void setExpectedSalary(String expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public String getReceivedCommission() {
        return receivedCommission;
    }
    public void setReceivedCommission(String receivedCommission) {
        this.receivedCommission = receivedCommission;
    }

    public boolean isPay() {
        return pay;
    }
    public void setPay(boolean pay) {
        this.pay = pay;
    }

    public String getSalary() {
        return salary;
    }
    public void setSalary(String salary) {
        this.salary = salary;
    }

    public boolean isPayAll() {
        return payAll;
    }
    public void setPayAll(boolean payAll) {
        this.payAll = payAll;
    }

    @Override
    public String getNote() {
        return note;
    }
    @Override
    public void setNote(String note) {
        this.note = note;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyId() {
        return companyId;
    }
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    private static String getStringValue(Long value) {
        return value != null ? String.valueOf(value) : null;
    }

    public boolean isEditable() {
        return editable;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}