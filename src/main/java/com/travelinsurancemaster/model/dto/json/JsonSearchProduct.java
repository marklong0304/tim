package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.dto.system.SystemSettings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 30.04.15.
 */
public class JsonSearchProduct {

    private String vendorName;
    private String vendorCode;
    private String policyMetaUniqueCode;
    private String policyMetaName;
    private String policyMetaCode;
    private String policyMetaCodeName;
    private Long policyMetaId;
    private BigDecimal totalPrice;
    private List<InnerCategory> categories;
    private int countOfTravelers;
    private boolean best;
    private boolean included;
    private boolean purchasable;

    public JsonSearchProduct(SystemSettings settings) {
        categories = new ArrayList<>();
        categories.add(new InnerCategory(settings.getPlanDescriptionCategory1()));
        categories.add(new InnerCategory(settings.getPlanDescriptionCategory2()));
        categories.add(new InnerCategory(settings.getPlanDescriptionCategory3()));
        categories.add(new InnerCategory(settings.getPlanDescriptionCategory4()));
        categories.add(new InnerCategory(settings.getPlanDescriptionCategory5()));
        categories.add(new InnerCategory(settings.getPlanDescriptionCategory6()));
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getPolicyMetaName() {
        return policyMetaName;
    }

    public void setPolicyMetaName(String policyMetaName) {
        this.policyMetaName = policyMetaName;
    }

    public Long getPolicyMetaId() {
        return policyMetaId;
    }

    public void setPolicyMetaId(Long policyMetaId) {
        this.policyMetaId = policyMetaId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<InnerCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<InnerCategory> categories) {
        this.categories = categories;
    }

    public int getCountOfTravelers() {
        return countOfTravelers;
    }

    public void setCountOfTravelers(int countOfTravelers) {
        this.countOfTravelers = countOfTravelers;
    }

    public boolean isBest() {
        return best;
    }

    public void setBest(boolean best) {
        this.best = best;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
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

    public String getPolicyMetaUniqueCode() {
        return policyMetaUniqueCode;
    }

    public void setPolicyMetaUniqueCode(String policyMetaUniqueCode) {
        this.policyMetaUniqueCode = policyMetaUniqueCode;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public void setPurchasable(boolean purchasable) {
        this.purchasable = purchasable;
    }

    public String getPolicyMetaCodeName() {
        return policyMetaCodeName;
    }

    public void setPolicyMetaCodeName(String policyMetaCodeName) {
        this.policyMetaCodeName = policyMetaCodeName;
    }

    public class InnerCategory {
        private String categoryName;
        private String categoryCode;
        private String categoryDescription;
        private String categoryValue;
        private String categoryCaption;

        public InnerCategory(Category category) {
            this.categoryCode = category.getCode();
            this.categoryName = category.getName();
            this.categoryDescription = "No";
            this.categoryValue = "No";
            this.categoryCaption = "No";
        }

        public String getCategoryCode() {
            return categoryCode;
        }

        public void setCategoryCode(String categoryCode) {
            this.categoryCode = categoryCode;
        }

        public String getCategoryDescription() {
            return categoryDescription;
        }

        public void setCategoryDescription(String categoryDescription) {
            this.categoryDescription = categoryDescription;
        }

        public String getCategoryValue() {
            return categoryValue;
        }

        public void setCategoryValue(String categoryValue) {
            this.categoryValue = categoryValue;
            this.categoryCaption = categoryValue;
        }

        public void setCategoryValue(PolicyMetaCategoryValue policyMetaCategoryValue) {
            this.categoryValue = policyMetaCategoryValue.getValue();
            this.categoryCaption = policyMetaCategoryValue.getCaption();
        }

        public String getCategoryCaption() {
            return categoryCaption;
        }

        public void setCategoryCaption(String categoryCaption) {
            this.categoryCaption = categoryCaption;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
    }


}
