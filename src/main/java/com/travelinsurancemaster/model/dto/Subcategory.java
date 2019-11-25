package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritchie on 7/5/16.
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subcategory implements Serializable, Comparable<Subcategory> {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String subcategoryName;

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String subcategoryCode;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "subcategory", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<SubcategoryValue> subcategoryValuesList = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(optional = false)
    private Category category;

    @Column(nullable = false)
    private String template;

    @Column(name = "sort_order", nullable = false)
    private Integer order;

    public Subcategory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public String getSubcategoryCode() {
        return subcategoryCode;
    }

    public void setSubcategoryCode(String subcategoryCode) {
        this.subcategoryCode = subcategoryCode;
    }

    public List<SubcategoryValue> getSubcategoryValuesList() {
        return subcategoryValuesList;
    }

    public void setSubcategoryValuesList(List<SubcategoryValue> subcategoryValuesList) {
        this.subcategoryValuesList = subcategoryValuesList;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int compareTo(Subcategory aThat) {
        if (this == aThat) {
            return 0;
        }
        if (aThat.getOrder() == null || this.getOrder() == null){
            return 0;
        }
        return Integer.compare(this.getOrder(), aThat.getOrder());
    }
}