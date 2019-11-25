package com.travelinsurancemaster.model.dto.cms.page;

import com.travelinsurancemaster.services.cms.IndexWhenPublishedInterceptor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 03.12.2015.
 */

@Entity
@Indexed(interceptor = IndexWhenPublishedInterceptor.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FilingClaimPage extends BasePage {

    @OneToOne
    @JoinColumn(name = "vendor_page_id")
    private VendorPage vendorPage;

    @NotEmpty
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Column(nullable = false)
    @Analyzer(definition = "customanalyzer")
    @Length(max = 50)
    private String phoneNumber;

    @NotEmpty
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Column(nullable = false)
    @Analyzer(definition = "customanalyzer")
    @Length(max = 255)
    private String schedulePerDay;

    @NotEmpty
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Column(nullable = false)
    @Analyzer(definition = "customanalyzer")
    @Length(max = 255)
    private String schedulePerWeek;

    @NotEmpty
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Column(nullable = true)
    @Analyzer(definition = "customanalyzer")
    @Length(max = 255)
    private String vendorNameFilClaims;

    @NotNull
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "filingClaimPage", fetch = FetchType.LAZY)
    private List<FilingClaimContact> filingClaimContacts = new ArrayList<>();

    public VendorPage getVendorPage() {
        return vendorPage;
    }

    public void setVendorPage(VendorPage vendorPage) {
        this.vendorPage = vendorPage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSchedulePerDay() {
        return schedulePerDay;
    }

    public void setSchedulePerDay(String schedulePerDay) {
        this.schedulePerDay = schedulePerDay;
    }

    public String getSchedulePerWeek() {
        return schedulePerWeek;
    }

    public void setSchedulePerWeek(String schedulePerWeek) {
        this.schedulePerWeek = schedulePerWeek;
    }

    public String getVendorNameFilClaims() { return vendorNameFilClaims; }

    public void setVendorNameFilClaims(String vendorNameFilClaims) {
        this.vendorNameFilClaims = vendorNameFilClaims;
    }

    public List<FilingClaimContact> getFilingClaimContacts() {
        return filingClaimContacts;
    }

    public void setFilingClaimContacts(List<FilingClaimContact> filingClaimContacts) {
        this.filingClaimContacts = filingClaimContacts;
    }
}
