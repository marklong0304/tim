package com.travelinsurancemaster.model.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ritchie on 5/14/15.
 */
@Entity
public class VendorCommission implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "Vendor expected commission is required!")
    @Column(nullable = false)
    private BigDecimal expectedCommission;

    @Column
    private String commissionValue;

    @Column
    @Enumerated(EnumType.STRING)
    private CommissionValueType valueType = CommissionValueType.FIX;

    @Column
    private BigDecimal receivedCommission;

    @Column(nullable = false)
    private boolean confirm;

    @Column
    private String description;

    @Column
    private String checkNumber;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column
    private Date receivedDate;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    public VendorCommission() {
    }

    public VendorCommission(VendorCommission vendorCommission, boolean full) {
        this.vendor = vendorCommission.getVendor();
        if(full) {
            this.expectedCommission = vendorCommission.getExpectedCommission();
            this.commissionValue = vendorCommission.getCommissionValue();
            this.valueType = vendorCommission.getValueType();
            this.receivedCommission = vendorCommission.getReceivedCommission();
            this.confirm = vendorCommission.isConfirm();
            this.description = vendorCommission.getDescription();
            this.checkNumber = vendorCommission.getCheckNumber();
            this.receivedDate = vendorCommission.getReceivedDate();
        } else {
            this.expectedCommission = new BigDecimal(0);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getExpectedCommission() {
        return expectedCommission;
    }

    public void setExpectedCommission(BigDecimal expectedCommission) {
        this.expectedCommission = expectedCommission;
    }

    public String getCommissionValue() {
        return commissionValue;
    }

    public void setCommissionValue(String commissionValue) {
        this.commissionValue = commissionValue;
    }

    public CommissionValueType getValueType() {
        return valueType;
    }

    public void setValueType(CommissionValueType valueType) {
        this.valueType = valueType;
    }

    public BigDecimal getReceivedCommission() {
        return receivedCommission;
    }

    public void setReceivedCommission(BigDecimal receivedCommission) {
        this.receivedCommission = receivedCommission;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
}
