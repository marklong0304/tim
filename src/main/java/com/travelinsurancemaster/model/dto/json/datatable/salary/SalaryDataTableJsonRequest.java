package com.travelinsurancemaster.model.dto.json.datatable.salary;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.purchase.PurchaseTravelerJson;
import com.travelinsurancemaster.model.util.BigDecimalRange;
import com.travelinsurancemaster.model.util.DateRange;
import com.travelinsurancemaster.model.util.datatable.DataTableField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 10.09.15.
 */
public class SalaryDataTableJsonRequest extends AbstractDataTableJsonRequest {
    private static final long serialVersionUID = 7759986619638810415L;

    private DateRange purchaseDate;
    private DateRange paidDate;
    private String policyNumber;
    private BigDecimalRange totalPrice;
    private BigDecimalRange expectedSalary;
    private BigDecimalRange receivedCommission;
    private BigDecimalRange salary;
    private Boolean pay;
    private String note;
    private PurchaseTravelerJson traveler;
    private List<DataTableField> updatedFields = new ArrayList<>();
    private Boolean cancellation;

    public DateRange getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(DateRange paidDate) {
        this.paidDate = paidDate;
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

    public BigDecimalRange getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(BigDecimalRange expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<DataTableField> getUpdatedFields() {
        return updatedFields;
    }

    public void setUpdatedFields(List<DataTableField> updatedFields) {
        this.updatedFields = updatedFields;
    }

    public BigDecimalRange getReceivedCommission() {
        return receivedCommission;
    }

    public void setReceivedCommission(BigDecimalRange receivedCommission) {
        this.receivedCommission = receivedCommission;
    }

    public BigDecimalRange getSalary() {
        return salary;
    }

    public void setSalary(BigDecimalRange salary) {
        this.salary = salary;
    }

    public Boolean isPay() {
        return pay;
    }

    public void setPay(Boolean pay) {
        this.pay = pay;
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

    public PurchaseTravelerJson getTraveler() {
        return traveler;
    }

    public void setTraveler(PurchaseTravelerJson traveler) {
        this.traveler = traveler;
    }

}
