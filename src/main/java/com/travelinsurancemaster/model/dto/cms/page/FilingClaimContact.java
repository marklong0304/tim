package com.travelinsurancemaster.model.dto.cms.page;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 03.12.2015.
 */

@Entity
public class FilingClaimContact extends BaseContent implements Serializable {
    private static final long serialVersionUID = -5829722123107441518L;

    @ManyToOne
    @JoinColumn(name = "filing_claim_page_id")
    private FilingClaimPage filingClaimPage;

    @Length(max = 255)
    @Column(nullable = false)
    private String customerServiceNumber;

    @Length(max = 255)
    @Column(nullable = false)
    private String customerServiceHoursOfOperation;

    @Length(max = 255)
    @Column(nullable = false)
    private String claimsFilingNumber;

    @Length(max = 255)
    @Column(nullable = false)
    private String claimsHoursOfOperation;

    @Length(max = 255)
    @Column(nullable = false)
    private String website;

    @Length(max = 255)
    @Column(nullable = false)
    private String email;

    @Length(max = 255)
    @Column(nullable = false)
    private String fax;

    @Length(max = 255)
    @Column(nullable = false)
    private String twentyFourHourEmergencyAssistanceNumbers;

    @Length(max = 255)
    @Column(nullable = false)
    private String mailTo;

    @OneToMany(mappedBy = "filingClaimContact", fetch = FetchType.LAZY)
    private List<PolicyMetaPage> policyMetaPages = new ArrayList<>();

    @Transient
    private String policyMetaPagesStr;

    public FilingClaimPage getFilingClaimPage() {
        return filingClaimPage;
    }

    public void setFilingClaimPage(FilingClaimPage filingClaimPage) {
        this.filingClaimPage = filingClaimPage;
    }

    public String getCustomerServiceNumber() {
        return customerServiceNumber;
    }

    public void setCustomerServiceNumber(String customerServiceNumber) {
        this.customerServiceNumber = customerServiceNumber;
    }

    public String getCustomerServiceHoursOfOperation() {
        return customerServiceHoursOfOperation;
    }

    public void setCustomerServiceHoursOfOperation(String customerServiceHoursOfOperation) {
        this.customerServiceHoursOfOperation = customerServiceHoursOfOperation;
    }

    public String getClaimsFilingNumber() {
        return claimsFilingNumber;
    }

    public void setClaimsFilingNumber(String claimsFilingNumber) {
        this.claimsFilingNumber = claimsFilingNumber;
    }

    public String getClaimsHoursOfOperation() {
        return claimsHoursOfOperation;
    }

    public void setClaimsHoursOfOperation(String claimsHoursOfOperation) {
        this.claimsHoursOfOperation = claimsHoursOfOperation;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getTwentyFourHourEmergencyAssistanceNumbers() {
        return twentyFourHourEmergencyAssistanceNumbers;
    }

    public void setTwentyFourHourEmergencyAssistanceNumbers(String twentyFourHourEmergencyAssistanceNumbers) {
        this.twentyFourHourEmergencyAssistanceNumbers = twentyFourHourEmergencyAssistanceNumbers;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public List<PolicyMetaPage> getPolicyMetaPages() {
        return policyMetaPages;
    }

    public void setPolicyMetaPages(List<PolicyMetaPage> policyMetaPages) {
        this.policyMetaPages = policyMetaPages;
    }

    public String getPolicyMetaPagesStr() {
        return policyMetaPagesStr;
    }

    public void setPolicyMetaPagesStr(String policyMetaPagesStr) {
        this.policyMetaPagesStr = policyMetaPagesStr;
    }
}
