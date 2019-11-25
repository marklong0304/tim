package com.travelinsurancemaster.model.dto.json.datatable.salary.report;

public class SalaryCorrectionJson {

    private String salaryToPay;
    private boolean pay = false;
    private String note;
    private String affiliateId;


    public String getSalaryToPay() {
        return salaryToPay;
    }

    public void setSalaryToPay(String salaryToPay) {
        this.salaryToPay = salaryToPay;
    }

    public boolean isPay() {
        return pay;
    }

    public void setPay(boolean pay) {
        this.pay = pay;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(String affiliateId) {
        this.affiliateId = affiliateId;
    }
}
