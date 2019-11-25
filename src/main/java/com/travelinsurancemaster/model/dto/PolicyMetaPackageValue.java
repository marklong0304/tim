package com.travelinsurancemaster.model.dto;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Alexander.Isaenco
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PolicyMetaPackageValue implements Serializable {
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
    private String value;

    @ManyToOne(optional = false)
    private PolicyMetaPackage policyMetaPackage;

    @ManyToOne(optional = false)
    private PolicyMetaCategory policyMetaCategory;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PolicyMetaPackage getPolicyMetaPackage() {
        return policyMetaPackage;
    }

    public void setPolicyMetaPackage(PolicyMetaPackage policyMetaPackage) {
        this.policyMetaPackage = policyMetaPackage;
    }

    public PolicyMetaCategory getPolicyMetaCategory() {
        return policyMetaCategory;
    }

    public void setPolicyMetaCategory(PolicyMetaCategory policyMetaCategory) {
        this.policyMetaCategory = policyMetaCategory;
    }
}
