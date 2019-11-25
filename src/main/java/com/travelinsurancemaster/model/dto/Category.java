package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.dto.cms.page.CategoryContent;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 2/11/15.
 */
@Entity
@EqualsAndHashCode(of = "code", callSuper = false)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Category extends BasicEntity implements Serializable {
    static final long serialVersionUID = 1L;

    private static final ValueType DEFAULT_VALUE_TYPE = ValueType.FIX;

    @Column
    @CreationTimestamp
    private Date created;

    @Column
    @UpdateTimestamp
    private Date modified;

    @NotNull
    @Column(nullable = false, unique = true)
    private String code;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType type = CategoryType.SIMPLE;

    @ManyToOne(optional = false)
    private Group group;

    @Column
    @Enumerated(EnumType.STRING)
    private ValueType valueType;

    @Column(nullable = false)
    private Integer filterOrder;

    @NotNull
    @Column(nullable = false)
    private boolean displayAsFilter;

    @Column(nullable = true)
    private String template;

    @Column(columnDefinition = "TEXT")
    private String categoryCondition;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<PolicyMetaCategory> policyMetaCategories = new ArrayList<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "category", fetch = FetchType.LAZY)
    private List<PolicyMetaCategoryContent> policyMetaCategoryContentList = new ArrayList<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "category", fetch = FetchType.LAZY)
    private List<Subcategory> subcategoriesList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<CategoryContent> categoryContent = new ArrayList<>();

    @Deprecated
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<CategoryValue> values = new ArrayList<>();

    @Transient
    private boolean canDelete;

    @Transient
    private String cantDeleteReason;

    @Transient
    private boolean sliderCategoryBoolean;

    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }

    public Date getModified() { return modified; }
    public void setModified(Date modified) { this.modified = modified; }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }
    public void setType(CategoryType type) {
        this.type = type;
    }

    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Subcategory> getSubcategoriesList() {
        Collections.sort(subcategoriesList);
        return subcategoriesList;
    }
    public void setSubcategoriesList(List<Subcategory> subcategoriesList) {
        this.subcategoriesList = subcategoriesList;
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaCategoryContentList() {
        return policyMetaCategoryContentList;
    }
    public void setPolicyMetaCategoryContentList(List<PolicyMetaCategoryContent> policyMetaCategoryContentList) {
        this.policyMetaCategoryContentList = policyMetaCategoryContentList;
    }

    public List<PolicyMetaCategory> getPolicyMetaCategories() {
        return policyMetaCategories;
    }
    public void setPolicyMetaCategories(List<PolicyMetaCategory> policyMetaCategories) {
        this.policyMetaCategories = policyMetaCategories;
    }

    public ValueType getValueType() {
        if (getType().equals(CategoryType.CATALOG) && valueType == null) {
            return DEFAULT_VALUE_TYPE;
        }
        return valueType;
    }
    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public Integer getFilterOrder() {
        return filterOrder;
    }
    public void setFilterOrder(Integer filterOrder) {
        this.filterOrder = filterOrder;
    }

    public boolean isDisplayAsFilter() {
        return displayAsFilter;
    }
    public boolean isNotDisplayAsFilter() {
        return !displayAsFilter;
    }
    public void setDisplayAsFilter(boolean displayAsFilter) {
        this.displayAsFilter = displayAsFilter;
    }

    public String getTemplate() {
        return template;
    }
    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isCanDelete() {
        return canDelete;
    }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }

    public String getCantDeleteReason() {
        return cantDeleteReason;
    }
    public void setCantDeleteReason(String cantDeleteReason) {
        this.cantDeleteReason = cantDeleteReason;
    }

    public boolean isSliderCategoryBoolean() {
        return sliderCategoryBoolean;
    }
    public void setSliderCategoryBoolean(boolean sliderCategoryBoolean) {
        this.sliderCategoryBoolean = sliderCategoryBoolean;
    }

    public List<CategoryContent> getCategoryContent() {
        return categoryContent;
    }
    public void setCategoryContent(List<CategoryContent> categoryContent) {
        this.categoryContent = categoryContent;
    }

    public String getCategoryCondition() {
        return categoryCondition;
    }
    public void setCategoryCondition(String categoryCondition) {
        this.categoryCondition = categoryCondition;
    }

    @Deprecated
    public List<CategoryValue> getValues() {
        return values;
    }
    @Deprecated
    public void setValues(List<CategoryValue> values) {
        this.values = values;
    }

    public enum CategoryType {
        SIMPLE, CATALOG, CONDITIONAL
    }
}