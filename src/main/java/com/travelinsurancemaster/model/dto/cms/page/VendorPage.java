package com.travelinsurancemaster.model.dto.cms.page;

import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.services.cms.IndexWhenPublishedInterceptor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chernov Artur on 15.10.15.
 */

@Entity
@Indexed(interceptor = IndexWhenPublishedInterceptor.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class VendorPage extends BasePage {

    @OneToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(nullable = false)
    private BigDecimal rating = BigDecimal.ZERO;

    @OneToMany(mappedBy = "vendorPage", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<PolicyMetaPage> policyMetaPages = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "vendorPage", cascade = CascadeType.REMOVE)
    private FilingClaimPage filingClaimPage;

    @Column(name = "deleted_date")
    private Date deletedDate;

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public List<PolicyMetaPage> getPolicyMetaPages() {
        return policyMetaPages;
    }

    public void setPolicyMetaPages(List<PolicyMetaPage> policyMetaPages) {
        this.policyMetaPages = policyMetaPages;
    }

    public FilingClaimPage getFilingClaimPage() {
        return filingClaimPage;
    }

    public void setFilingClaimPage(FilingClaimPage filingClaimPage) {
        this.filingClaimPage = filingClaimPage;
    }

    public Date getDeletedDate() { return deletedDate; }

    public void setDeletedDate(Date deletedDate) { this.deletedDate = deletedDate; }
}
