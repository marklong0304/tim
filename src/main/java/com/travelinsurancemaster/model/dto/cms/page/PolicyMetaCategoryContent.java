package com.travelinsurancemaster.model.dto.cms.page;

import com.travelinsurancemaster.model.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Chernov Artur on 20.10.15.
 */

@Entity

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMetaCategoryContent extends BaseContent {

    @ManyToOne
    @JoinColumn(name = "policy_meta_category_id", referencedColumnName = "id")
    private PolicyMetaPage policyMetaPageForCategory;

    @ManyToOne
    @JoinColumn(name = "policy_meta_plan_info_id", referencedColumnName = "id")
    private PolicyMetaPage policyMetaPageForPlanInfo;

    @ManyToOne
    @JoinColumn(name = "policy_meta_custom_category_id", referencedColumnName = "id")
    private PolicyMetaPage policyMetaPageForCustomCategory;

    @ManyToOne
    @JoinColumn(name = "policy_meta_restrictions_id", referencedColumnName = "id")
    private PolicyMetaPage policyMetaPageForRestrictions;

    @ManyToOne
    @JoinColumn(name = "policy_meta_package_id", referencedColumnName = "id")
    private PolicyMetaPage policyMetaPackage;

    @OneToOne
    @JoinColumn(name = "package_options_id", referencedColumnName = "id")
    private PolicyMetaPackage packageOptions;

    @ManyToOne
    private Category category;

    @Column
    private String group;

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.YES, store = Store.YES)
    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "TEXT")
    @Analyzer(definition = "htmlalyzer")
    @Length(max = 100000)
    private String certificateText;

    @Transient
    private Set<String> plansSelected = new HashSet<>();

    @Transient
    private List<PolicyMetaCategoryValue> valuesFromPolicyMetaApi = new ArrayList<>();

    @Transient
    private boolean upsaleCategory = false;

    @Transient
    private boolean canDeleted = true;


    public PolicyMetaCategoryContent() {
    }

    public PolicyMetaCategoryContent(PolicyMeta policyMeta, PolicyMetaCategory policyMetaCategory) {
        this.setCategory(policyMetaCategory.getCategory());
        this.setContent(StringUtils.EMPTY);
        this.setName(policyMetaCategory.getCategory().getName());
        this.setPolicyMetaPageForCategory(policyMeta.getPolicyMetaPage());
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCertificateText() {
        return certificateText;
    }

    public void setCertificateText(String certificateText) {
        this.certificateText = certificateText;
    }

    public PolicyMetaPage getPolicyMetaPageForCategory() {
        return policyMetaPageForCategory;
    }

    public void setPolicyMetaPageForCategory(PolicyMetaPage policyMetaPageForCategory) {
        this.policyMetaPageForCategory = policyMetaPageForCategory;
    }

    public PolicyMetaPage getPolicyMetaPageForPlanInfo() {
        return policyMetaPageForPlanInfo;
    }

    public void setPolicyMetaPageForPlanInfo(PolicyMetaPage policyMetaPageForPlanInfo) {
        this.policyMetaPageForPlanInfo = policyMetaPageForPlanInfo;
    }

    public PolicyMetaPage getPolicyMetaPageForCustomCategory() {
        return policyMetaPageForCustomCategory;
    }

    public void setPolicyMetaPageForCustomCategory(PolicyMetaPage policyMetaPageForCustomCategory) {
        this.policyMetaPageForCustomCategory = policyMetaPageForCustomCategory;
    }

    public PolicyMetaPage getPolicyMetaPageForRestrictions() {
        return policyMetaPageForRestrictions;
    }

    public void setPolicyMetaPageForRestrictions(PolicyMetaPage policyMetaPageForRestrictions) {
        this.policyMetaPageForRestrictions = policyMetaPageForRestrictions;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPlansSelected() {
        return plansSelected;
    }

    public void setPlansSelected(Set<String> plansSelected) {
        this.plansSelected = plansSelected;
    }

    public List<PolicyMetaCategoryValue> getValuesFromPolicyMetaApi() {
        return valuesFromPolicyMetaApi;
    }

    public void setValuesFromPolicyMetaApi(List<PolicyMetaCategoryValue> valuesFromPolicyMetaApi) {
        this.valuesFromPolicyMetaApi = valuesFromPolicyMetaApi;
    }

    public boolean isUpsaleCategory() {
        return upsaleCategory;
    }

    public void setUpsaleCategory(boolean upsaleCategory) {
        this.upsaleCategory = upsaleCategory;
    }

    public boolean isCanDeleted() {
        return canDeleted;
    }

    public void setCanDeleted(boolean canDeleted) {
        this.canDeleted = canDeleted;
    }

    public PolicyMetaPage getPolicyMetaPackage() { return policyMetaPackage; }

    public void setPolicyMetaPackage(PolicyMetaPage policyMetaPackage) { this.policyMetaPackage = policyMetaPackage; }

    public PolicyMetaPackage getPackageOptions() { return packageOptions; }

    public void setPackageOptions(PolicyMetaPackage packageOptions) { this.packageOptions = packageOptions; }

}
