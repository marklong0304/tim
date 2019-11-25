package com.travelinsurancemaster.model.dto.json.datatable.salary;

import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.dto.SalaryCorrection;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import lombok.Data;

@Data
public class SalarySearchJson {
    private String affiliate;
    private Long affiliateId;
    private String salary;
    private String payDate;
    private String company;
    private String companyId;
    private String id;

    public SalarySearchJson(Purchase purchase) {

        this.id = purchase.getPurchaseUuid();

        this.affiliate = purchase.getAffiliateCommission().getAffiliate().getFullName();
        this.affiliateId = purchase.getAffiliateCommission().getAffiliate().getId();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        this.payDate = dateFormat.format(purchase.getAffiliateCommission().getPaid());

        BigDecimal salary = purchase.getAffiliateCommission().getSalaryToPay();
        this.salary = salary != null ? salary.toString() : StringUtils.EMPTY;

        Company company = purchase.getAffiliateCommission().getAffiliate().getCompany();
        this.company = company != null ? company.getName() : StringUtils.EMPTY;
        this.companyId = company != null ? company.getId().toString() : StringUtils.EMPTY;
    }

    public SalarySearchJson(SalaryCorrection correction) {

        this.affiliate = correction.getAffiliate().getFullName();
        this.affiliateId = correction.getAffiliate().getId();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        this.payDate = dateFormat.format(correction.getPaid());
        this.salary = correction.getSalaryToPay().toString();

        Company company = correction.getAffiliate().getCompany();
        this.company = company != null ? company.getName() : StringUtils.EMPTY;
        this.companyId = company != null ? company.getId().toString() : StringUtils.EMPTY;
    }
}
