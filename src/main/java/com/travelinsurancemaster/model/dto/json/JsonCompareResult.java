package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;

import java.util.*;

/**
 * Created by artur on 16.09.2016.
 */
public class JsonCompareResult {
    private static final String DEFAULT_SORT_ORDER = "lth";

    private List<JsonCompareProduct> products = new ArrayList<>();
    private List<JsonCategory> categories = new ArrayList<>();
    private List<Certificate> certificateList = new ArrayList<>();
    private List<Boolean> zeroToApiList = new ArrayList<>();
    private List<Boolean> minTripCostList = new ArrayList<>();
    private List<String> includedPolicies = new ArrayList<>();
    private List<Map<String, PolicyMetaCategory>> policyCategoriesWithCertificateTextList = new ArrayList<>();
    private List<JsonGroup> jsonGroups = new ArrayList<>();
    private String bestPlanCode;
    private String sortOrder = DEFAULT_SORT_ORDER;
    private boolean depositAndPaymentDates = false;
    private boolean showCountAfterEnabledFilter;
    private String quoteRequestJson;
    private boolean finished = false;
    private String requestId;
    private boolean zeroCost = false;
    private PlanType planType;

    public JsonCompareResult() {
    }

    public JsonCompareResult(boolean depositAndPaymentDates, String requestId, List<String> includedPolicies) {
        this.depositAndPaymentDates = depositAndPaymentDates;
        this.requestId = requestId;
        this.includedPolicies = includedPolicies;
    }

    public List<JsonCompareProduct> getProducts() {
        return products;
    }

    public void setProducts(List<JsonCompareProduct> products) {
        this.products = products;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<Certificate> getCertificateList() {
        return certificateList;
    }

    public void setCertificateList(List<Certificate> certificateList) {
        this.certificateList = certificateList;
    }

    public List<Boolean> getZeroToApiList() {
        return zeroToApiList;
    }

    public void setZeroToApiList(List<Boolean> zeroToApiList) {
        this.zeroToApiList = zeroToApiList;
    }

    public List<Boolean> getMinTripCostList() {
        return minTripCostList;
    }

    public void setMinTripCostList(List<Boolean> minTripCostList) {
        this.minTripCostList = minTripCostList;
    }

    public String getBestPlanCode() {
        return bestPlanCode;
    }

    public void setBestPlanCode(String bestPlanCode) {
        this.bestPlanCode = bestPlanCode;
    }

    public List<Map<String, PolicyMetaCategory>> getPolicyCategoriesWithCertificateTextList() {
        return policyCategoriesWithCertificateTextList;
    }

    public void setPolicyCategoriesWithCertificateTextList(List<Map<String, PolicyMetaCategory>> policyCategoriesWithCertificateTextList) {
        this.policyCategoriesWithCertificateTextList = policyCategoriesWithCertificateTextList;
    }

    public String getQuoteRequestJson() {
        return quoteRequestJson;
    }

    public void setQuoteRequestJson(String quoteRequestJson) {
        this.quoteRequestJson = quoteRequestJson;
    }

    public List<JsonCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<JsonCategory> categories) {
        this.categories = categories;
    }

    public boolean isDepositAndPaymentDates() {
        return depositAndPaymentDates;
    }

    public void setDepositAndPaymentDates(boolean depositAndPaymentDates) {
        this.depositAndPaymentDates = depositAndPaymentDates;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<String> getIncludedPolicies() {
        return includedPolicies;
    }

    public void setIncludedPolicies(List<String> includedPolicies) {
        this.includedPolicies = includedPolicies;
    }

    public boolean isShowCountAfterEnabledFilter() {
        return showCountAfterEnabledFilter;
    }

    public void setShowCountAfterEnabledFilter(boolean showCountAfterEnabledFilter) {
        this.showCountAfterEnabledFilter = showCountAfterEnabledFilter;
    }

    public List<JsonGroup> getJsonGroups() {
        return jsonGroups;
    }

    public void setJsonGroups(List<JsonGroup> jsonGroups) {
        this.jsonGroups = jsonGroups;
    }

    public boolean isZeroCost() {
        return zeroCost;
    }

    public void setZeroCost(boolean zeroCost) {
        this.zeroCost = zeroCost;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }
}
