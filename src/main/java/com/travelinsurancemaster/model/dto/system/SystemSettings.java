package com.travelinsurancemaster.model.dto.system;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.PercentInfo;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 25.08.15.
 */

@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SystemSettings implements Serializable {
    private static final long serialVersionUID = 1855367095313285793L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String phone;

    @Column
    @Enumerated(EnumType.STRING)
    private PercentType defaultLinkPercentType = PercentType.NONE;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "default_link_percent_info", joinColumns = @JoinColumn(name = "system_settings_id"))
    private List<PercentInfo> defaultLinkPercentInfo = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "category1_id", nullable = false)
    private Category planDescriptionCategory1;

    @OneToOne
    @JoinColumn(name = "category2_id", nullable = false)
    private Category planDescriptionCategory2;

    @OneToOne
    @JoinColumn(name = "category3_id", nullable = false)
    private Category planDescriptionCategory3;

    @OneToOne
    @JoinColumn(name = "category4_id", nullable = false)
    private Category planDescriptionCategory4;

    @OneToOne
    @JoinColumn(name = "category5_id", nullable = false)
    private Category planDescriptionCategory5;

    @OneToOne
    @JoinColumn(name = "category6_id", nullable = false)
    private Category planDescriptionCategory6;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PercentType getDefaultLinkPercentType() {
        return defaultLinkPercentType;
    }

    public void setDefaultLinkPercentType(PercentType defaultLinkPercentType) {
        this.defaultLinkPercentType = defaultLinkPercentType;
    }

    public List<PercentInfo> getDefaultLinkPercentInfo() {
        return defaultLinkPercentInfo;
    }

    public void setDefaultLinkPercentInfo(List<PercentInfo> defaultLinkPercentInfo) {
        this.defaultLinkPercentInfo = defaultLinkPercentInfo;
    }

    public Category getPlanDescriptionCategory1() {
        return planDescriptionCategory1;
    }

    public void setPlanDescriptionCategory1(Category planDescriptionCategory1) {
        this.planDescriptionCategory1 = planDescriptionCategory1;
    }

    public Category getPlanDescriptionCategory2() {
        return planDescriptionCategory2;
    }

    public void setPlanDescriptionCategory2(Category planDescriptionCategory2) {
        this.planDescriptionCategory2 = planDescriptionCategory2;
    }

    public Category getPlanDescriptionCategory3() {
        return planDescriptionCategory3;
    }

    public void setPlanDescriptionCategory3(Category planDescriptionCategory3) {
        this.planDescriptionCategory3 = planDescriptionCategory3;
    }

    public Category getPlanDescriptionCategory4() {
        return planDescriptionCategory4;
    }

    public void setPlanDescriptionCategory4(Category planDescriptionCategory4) {
        this.planDescriptionCategory4 = planDescriptionCategory4;
    }

    public Category getPlanDescriptionCategory5() {
        return planDescriptionCategory5;
    }

    public void setPlanDescriptionCategory5(Category planDescriptionCategory5) {
        this.planDescriptionCategory5 = planDescriptionCategory5;
    }

    public Category getPlanDescriptionCategory6() {
        return planDescriptionCategory6;
    }

    public void setPlanDescriptionCategory6(Category planDescriptionCategory6) {
        this.planDescriptionCategory6 = planDescriptionCategory6;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
