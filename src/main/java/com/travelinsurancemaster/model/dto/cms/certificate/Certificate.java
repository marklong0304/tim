package com.travelinsurancemaster.model.dto.cms.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.security.User;
import org.apache.commons.io.FileUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chernov Artur on 15.12.2015.
 */

@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Certificate implements Serializable{
    private static final long serialVersionUID = 7168475486335083439L;

    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, name = "file_uid")
    private String uuid;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(nullable = false)
    private Date createDate;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(nullable = false)
    private Date modifiedDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private boolean deleted;

    @Column(nullable = false)
    private boolean defaultPolicy;

    @Column(nullable = false)
    private String vendorCode;

    @Column(nullable = false)
    private String policyMetaCode;

    @JsonIgnore
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = CountryCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "certificate_country", joinColumns = @JoinColumn(name = "certificate_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<CountryCode> countries = new HashSet<>();

    @JsonIgnore
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = StateCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "certificate_state", joinColumns = @JoinColumn(name = "certificate_id"))
    @Column
    @Enumerated(EnumType.STRING)
    private Set<StateCode> states = new HashSet<>();

    @Transient
    private String prettySize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getPolicyMetaCode() {
        return policyMetaCode;
    }

    public void setPolicyMetaCode(String policyMetaCode) {
        this.policyMetaCode = policyMetaCode;
    }

    public Set<CountryCode> getCountries() {
        return countries;
    }

    public void setCountries(Set<CountryCode> countries) {
        this.countries = countries;
    }

    public Set<StateCode> getStates() {
        return states;
    }

    public void setStates(Set<StateCode> states) {
        this.states = states;
    }

    public boolean isDefaultPolicy() {
        return defaultPolicy;
    }

    public void setDefaultPolicy(boolean defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    public String getPrettySize() {
        return FileUtils.byteCountToDisplaySize(size);
    }

}
