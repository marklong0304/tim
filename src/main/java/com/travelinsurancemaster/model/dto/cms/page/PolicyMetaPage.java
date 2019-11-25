package com.travelinsurancemaster.model.dto.cms.page;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.services.cms.IndexWhenPublishedInterceptor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chernov Artur on 18.10.15.
 */
@Entity
@Indexed(interceptor = IndexWhenPublishedInterceptor.class)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMetaPage extends BasePage {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_page_id")
    private VendorPage vendorPage;

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaPageForCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PolicyMetaCategoryContent> policyMetaCategoryList = new ArrayList<>();

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaPageForPlanInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PolicyMetaCategoryContent> policyMetaPlanInfoList = new ArrayList<>();

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaPageForCustomCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PolicyMetaCategoryContent> policyMetaCustomCategoryList = new ArrayList<>();

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaPageForRestrictions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PolicyMetaCategoryContent> policyMetaRestrictionsList = new ArrayList<>();

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToOne(mappedBy = "policyMetaPage", fetch = FetchType.LAZY)
    private PolicyMeta policyMeta;

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "policyMetaPackage", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PolicyMetaCategoryContent> policyMetaPackageList = new ArrayList<>();

    @Deprecated
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column
    private byte[] certificate;

    @Deprecated
    @Column
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date certificateModifiedDate;

    @ManyToOne
    @JoinColumn(name = "filling_claim_contact_id")
    private FilingClaimContact filingClaimContact;

    public VendorPage getVendorPage() {
        return vendorPage;
    }

    public void setVendorPage(VendorPage vendorPage) {
        this.vendorPage = vendorPage;
    }

    public PolicyMeta getPolicyMeta() {
        return policyMeta;
    }

    public void setPolicyMeta(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
    }

    @Deprecated
    public byte[] getCertificate() {
        return certificate;
    }

    @Deprecated
    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    @Deprecated
    public Date getCertificateModifiedDate() {
        return certificateModifiedDate;
    }

    @Deprecated
    public void setCertificateModifiedDate(Date certificateModifiedDate) {
        this.certificateModifiedDate = certificateModifiedDate;
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaCategoryList() {
        return policyMetaCategoryList;
    }

    public void setPolicyMetaCategoryList(List<PolicyMetaCategoryContent> policyMetaCategoryList) {
        this.policyMetaCategoryList = policyMetaCategoryList;
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaPlanInfoList() {
        return policyMetaPlanInfoList;
    }

    public void setPolicyMetaPlanInfoList(List<PolicyMetaCategoryContent> policyMetaPlanInfoList) {
        this.policyMetaPlanInfoList = policyMetaPlanInfoList;
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaCustomCategoryList() {
        return policyMetaCustomCategoryList;
    }

    public void setPolicyMetaCustomCategoryList(List<PolicyMetaCategoryContent> policyMetaCustomCategoryList) {
        this.policyMetaCustomCategoryList = policyMetaCustomCategoryList;
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaRestrictionsList() {
        return policyMetaRestrictionsList;
    }

    public void setPolicyMetaRestrictionsList(List<PolicyMetaCategoryContent> policyMetaRestrictionsList) {
        this.policyMetaRestrictionsList = policyMetaRestrictionsList;
    }

    public FilingClaimContact getFilingClaimContact() {
        return filingClaimContact;
    }

    public void setFilingClaimContact(FilingClaimContact filingClaimContact) {
        this.filingClaimContact = filingClaimContact;
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaPackageList() { return policyMetaPackageList; }

    public void setPolicyMetaPackageList(List<PolicyMetaCategoryContent> policyMetaPackageList) { this.policyMetaPackageList = policyMetaPackageList; }
}
