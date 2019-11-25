package com.travelinsurancemaster.model.dto.json.datatable.commission.report;

import com.travelinsurancemaster.model.CommissionState;
import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.purchase.PurchaseTravelerJson;
import com.travelinsurancemaster.model.util.BigDecimalRange;
import com.travelinsurancemaster.model.util.DateRange;
import com.travelinsurancemaster.model.util.datatable.DataTableField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 30.09.15.
 */
public class CommissionReportDataTableJsonRequest extends AbstractDataTableJsonRequest {
    private static final long serialVersionUID = -2338938990565110508L;

    private List<Long> affiliates = new ArrayList<>();
    private DateRange departDate;
    private DateRange purchaseDate;
    private String policyNumber;
    private BigDecimalRange expectedCommission;
    private BigDecimalRange totalPrice;
    private CommissionState confirm;
    private String checkNumber;
    private BigDecimalRange receivedCommission;
    private DateRange receivedDate;
    private String note;
    private PurchaseTravelerJson traveler;
    private List<DataTableField> updatedFields = new ArrayList<>();
    private Boolean cancellation;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<Long> getAffiliates() {
        return affiliates;
    }

    public void setAffiliates(List<Long> affiliates) {
        this.affiliates = affiliates;
    }

    public DateRange getDepartDate() {
        return departDate;
    }

    public void setDepartDate(DateRange departDate) {
        this.departDate = departDate;
    }

    public DateRange getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(DateRange purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public PurchaseTravelerJson getTraveler() {
        return traveler;
    }

    public void setTraveler(PurchaseTravelerJson traveler) {
        this.traveler = traveler;
    }

    public BigDecimalRange getExpectedCommission() {
        return expectedCommission;
    }

    public void setExpectedCommission(BigDecimalRange expectedCommission) {
        this.expectedCommission = expectedCommission;
    }

    public CommissionState getConfirm() {
        return confirm;
    }

    public void setConfirm(CommissionState confirm) {
        this.confirm = confirm;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public BigDecimalRange getReceivedCommission() {
        return receivedCommission;
    }

    public void setReceivedCommission(BigDecimalRange receivedCommission) {
        this.receivedCommission = receivedCommission;
    }

    public DateRange getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(DateRange receivedDate) {
        this.receivedDate = receivedDate;
    }


    public List<DataTableField> getUpdatedFields() {
        return updatedFields;
    }

    public void setUpdatedFields(List<DataTableField> updatedFields) {
        this.updatedFields = updatedFields;
    }

    public Boolean isCancellation() {
        return cancellation;
    }

    public void setCancellation(Boolean cancellation) {
        this.cancellation = cancellation;
    }

    public BigDecimalRange getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimalRange totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Boolean getCancellation() {
        return cancellation;
    }
}
