package com.travelinsurancemaster.model.webservice.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.services.InsuranceClientFacade;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Vlad on 10.02.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product implements Comparable<Product>, Serializable {
    private static final long serialVersionUID = 7270207893485724250L;

    private PolicyMeta policyMeta;
    private PolicyMetaCode policyMetaCode;
    private BigDecimal totalPrice;
    private String certificate;
    private String quoteIdentifier;
    private String quoteVersion;
    private Long policyId;
    private String requestId;

    private List<Result.Error> errors = new ArrayList<>();
    @JsonIgnore
    private boolean hasCancellation;
    @JsonIgnore
    private boolean hasPrimaryMedical;

    @JsonIgnore
    private Map<String, String> upsaleValueMap = new HashMap<>();

    @JsonIgnore
    private Map<String, PolicyMetaCategoryValue> categoryUpsaleValues = new HashMap<>();

    public Product() {
    }

    public Product(List<Result.Error> errors) {
        this.errors = errors;
    }

    public Product(PolicyMeta policyMeta, PolicyMetaCode policyMetaCode, BigDecimal totalPrice) {
        this(policyMeta, policyMetaCode, totalPrice, Collections.emptyMap());
    }

    public Product(PolicyMeta policyMeta, PolicyMetaCode policyMetaCode, BigDecimal totalPrice, Map<String, String> upsaleValueMap) {
        this(policyMeta, policyMetaCode, totalPrice, upsaleValueMap, null);
    }

    public Product(PolicyMeta policyMeta, PolicyMetaCode policyMetaCode, BigDecimal totalPrice, Map<String, String> upsaleValueMap, String certificate) {
        if (policyMeta == null) {
            throw new NullPointerException("policyMeta is null");
        }
        if (policyMetaCode == null) {
            throw new NullPointerException("policyMetaCode is null");
        }
        if (totalPrice == null) {
            throw new NullPointerException("totalPrice is null");
        }
        this.policyMeta = policyMeta;
        this.policyMetaCode = policyMetaCode;
        this.totalPrice = totalPrice;
        this.upsaleValueMap.putAll(upsaleValueMap);
        this.certificate = certificate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PolicyMeta getPolicyMeta() {
        return policyMeta;
    }

    public void setPolicyMeta(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
    }

    public List<Result.Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Result.Error> errors) {
        this.errors = errors;
    }

    public PolicyMetaCode getPolicyMetaCode() {
        return policyMetaCode;
    }

    public void setPolicyMetaCode(PolicyMetaCode policyMetaCode) {
        this.policyMetaCode = policyMetaCode;
    }

    public Map<String, PolicyMetaCategoryValue> getCategoryUpsaleValues() {
        return categoryUpsaleValues;
    }

    public void setCategoryUpsaleValues(Map<String, PolicyMetaCategoryValue> categoryUpsaleValues) {
        this.categoryUpsaleValues = categoryUpsaleValues;
    }

    public Map<String, String> getUpsaleValueMap() {
        return upsaleValueMap;
    }

    public void setUpsaleValueMap(Map<String, String> upsaleValueMap) {
        this.upsaleValueMap = upsaleValueMap;
    }

    public boolean isHasCancellation() {
        return hasCancellation;
    }

    public void setHasCancellation(boolean hasCancellation) {
        this.hasCancellation = hasCancellation;
    }

    public boolean isHasPrimaryMedical() {
        return hasPrimaryMedical;
    }

    public void setHasPrimaryMedical(boolean hasPrimaryMedical) {
        this.hasPrimaryMedical = hasPrimaryMedical;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getQuoteIdentifier() { return quoteIdentifier; }

    public void setQuoteIdentifier(String quoteIdentifier) { this.quoteIdentifier = quoteIdentifier; }

    public String getQuoteVersion() { return quoteVersion; }

    public void setQuoteVersion(String quoteVersion) { this.quoteVersion = quoteVersion; }

    public Long getPolicyId() { return policyId; }

    public void setPolicyId(Long policyId) { this.policyId = policyId; }

    public String getRequestId() { return requestId; }

    public void setRequestId(String requestId) { this.requestId = requestId; }

    @Override
    public int compareTo(Product product) {
        if (this.totalPrice == null)
            if (product.totalPrice == null)
                return 0;
            else
                return -1;
        else if (product.totalPrice == null)
            return 1;
        else
            return this.totalPrice.compareTo(product.getTotalPrice());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Product)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        Product rhs = (Product) obj;
        EqualsBuilder eqb = new EqualsBuilder().append(totalPrice, rhs.totalPrice);
        if (policyMeta != null && rhs.policyMeta != null) {
            eqb.append(policyMeta.getId(), rhs.policyMeta.getId());
        }
        if (StringUtils.isNotEmpty(policyMetaCode.getCode())) {
            eqb.append(policyMetaCode, rhs.policyMetaCode);
        }
        return eqb.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
        hcb.append(totalPrice);
        if (policyMeta != null) {
            hcb.append(policyMeta.getId());
        }
        if (policyMetaCode != null) {
            hcb.append(policyMetaCode);
        }
        return hcb.toHashCode();
    }

    @JsonIgnore
    public boolean isTimeout() {
        for (Result.Error e : errors) {
            if (e.getErrorCode().equals(InsuranceClientFacade.TIMEOUT_ERROR)) {
                return true;
            }
        }
        return false;
    }
}
