package com.travelinsurancemaster.model.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.dto.AffiliateLink;
import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.dto.UserInfo;
import com.travelinsurancemaster.model.dto.cms.attachment.FileAttachment;
import com.travelinsurancemaster.model.dto.cms.page.Page;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Chernov Artur on 17.04.15.
 */

@Entity
public class User implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column
    @UpdateTimestamp
    private Date created;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Transient
    private String repeatPassword;

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    private Company company;

    @Column(nullable = false, name = "role")
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = {@JoinColumn(name = "user_id")})
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_info_id", nullable = false)
    private UserInfo userInfo = new UserInfo();

    @JsonIgnore
    @OneToMany(mappedBy = "user",
            targetEntity = VerificationToken.class,
            cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<VerificationToken> verificationTokens = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user",
            targetEntity = AffiliateLink.class,
            cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AffiliateLink> affiliateLinks = new ArrayList<>();

    @Column(nullable = false)
    private boolean isVerified = false;

    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Page> pages = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<FileAttachment> files = new ArrayList<>();

    @Column
    private Date affiliationRequested;

    @Column
    private Date affiliationApproved;

    @Column
    private Date affiliationNotified;

    @Column
    private Date deleted;

    @Column
    private String deletedBy;

    @Transient
    private boolean companyContactUser;

    @Transient
    private boolean canDelete;

    @Transient
    private boolean hasBookings;

    @Transient
    private String fullName;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }
    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isVerified() {
        return isVerified;
    }
    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public List<VerificationToken> getVerificationTokens() {
        return verificationTokens;
    }
    public void setVerificationTokens(List<VerificationToken> verificationTokens) {
        this.verificationTokens = verificationTokens;
    }

    public synchronized void addVerificationToken(VerificationToken token) {
        verificationTokens.add(token);
    }
    public VerificationToken getActiveLostPasswordToken() {
        return getActiveToken(VerificationToken.VerificationTokenType.lostPassword);
    }

    public VerificationToken getActiveEmailVerificationToken() {
        return getActiveToken(VerificationToken.VerificationTokenType.emailVerification);
    }
    public VerificationToken getActiveEmailRegistrationToken() {
        return getActiveToken(VerificationToken.VerificationTokenType.emailRegistration);
    }

    private VerificationToken getActiveToken(VerificationToken.VerificationTokenType tokenType) {
        VerificationToken activeToken = null;
        for (VerificationToken token : getVerificationTokens()) {
            if (token.getTokenType().equals(tokenType)
                    && !token.hasExpired() && !token.isVerified()) {
                activeToken = token;
                break;
            }
        }
        return activeToken;
    }

    public List<Page> getPages() {
        return pages;
    }
    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public List<FileAttachment> getFiles() {
        return files;
    }
    public void setFiles(List<FileAttachment> files) {
        this.files = files;
    }

    public List<AffiliateLink> getAffiliateLinks() {
        return affiliateLinks;
    }
    public void setAffiliateLinks(List<AffiliateLink> affiliateLinks) {
        this.affiliateLinks = affiliateLinks;
    }

    public Date getAffiliationRequested() { return affiliationRequested; }
    public void setAffiliationRequested(Date affiliationRequested) { this.affiliationRequested = affiliationRequested; }

    public Date getAffiliationApproved() { return affiliationApproved; }
    public void setAffiliationApproved(Date affiliationApproved) { this.affiliationApproved = affiliationApproved; }

    public Date getAffiliationNotified() { return affiliationNotified; }
    public void setAffiliationNotified(Date affiliationNotified) { this.affiliationNotified = affiliationNotified; }

    public Date getDeleted() { return deleted; }
    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public boolean isCanDelete() {
        return canDelete;
    }
    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isHasBookings() {
        return hasBookings;
    }
    public void setHasBookings(boolean hasBookings) {
        this.hasBookings = hasBookings;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean isAffiliate() { return hasRole(Role.ROLE_AFFILIATE); }

    public boolean hasCompensation() { return company == null && isAffiliate(); }

    public boolean isCompanyManager() { return hasRole(Role.ROLE_COMPANY_MANAGER); }

    public boolean isAdmin() { return hasRole(Role.ROLE_ADMIN); }

    public String getFullName(){
        UserInfo userInfo = this.getUserInfo();

        String fullName = this.getName();

        String lastName = userInfo.getLastName();
        if (lastName != null) {
            fullName = fullName + " " + lastName;
        }

        if (userInfo.isCompany()){
            fullName = (company != null ? company.getName() : "-") + " (" +  fullName + ")"; // for supporting old payments
        }

        return fullName;
    }
    public void setFullName(String fullName) { this.fullName = fullName; }
}