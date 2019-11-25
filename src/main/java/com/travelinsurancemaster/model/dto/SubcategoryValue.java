package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by ritchie on 7/5/16.
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SubcategoryValue implements Comparable<SubcategoryValue>, Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @CreationTimestamp
    private Date created;

    @Column
    @UpdateTimestamp
    private Date modified;

    @Column(nullable = false)
    private String subcategoryValue;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Subcategory subcategory;

    @JsonIgnore
    @ManyToOne(optional = false)
    private PolicyMetaCategoryValue policyMetaCategoryValue;

    public SubcategoryValue() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }

    public Date getModified() { return modified; }

    public void setModified(Date modified) { this.modified = modified; }

    public String getSubcategoryValue() {
        return subcategoryValue;
    }

    public void setSubcategoryValue(String subcategoryValue) {
        this.subcategoryValue = subcategoryValue;
    }

    public PolicyMetaCategoryValue getPolicyMetaCategoryValue() {
        return policyMetaCategoryValue;
    }

    public void setPolicyMetaCategoryValue(PolicyMetaCategoryValue policyMetaCategoryValue) {
        this.policyMetaCategoryValue = policyMetaCategoryValue;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    @Override
    public int compareTo(SubcategoryValue aThat) {
        if (this == aThat) {
            return 0;
        }
        if (aThat.getSubcategory() == null || this.getSubcategory() == null) {
            return 0;
        }
        if (aThat.getSubcategory().getOrder() == null || this.getSubcategory().getOrder() == null){
            return 0;
        }
        return Integer.compare(this.getSubcategory().getOrder(), aThat.getSubcategory().getOrder());
    }
}
