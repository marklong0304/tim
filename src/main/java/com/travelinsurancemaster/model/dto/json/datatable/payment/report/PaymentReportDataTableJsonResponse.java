package com.travelinsurancemaster.model.dto.json.datatable.payment.report;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonResponse;

import java.util.List;

public class PaymentReportDataTableJsonResponse extends AbstractDataTableJsonResponse {
    private List<PaymentReportJson> data;

    public List<PaymentReportJson> getData() {
        return data;
    }

    public void setData(List<PaymentReportJson> data) {
        this.data = data;
    }
}
